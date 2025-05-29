package com.runvpn.app.android.utils


import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import kotlin.experimental.inv


class IPRange : Comparable<IPRange> {
    private val mBitmask = byteArrayOf(0x80.toByte(), 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01)
    private lateinit var mFrom: ByteArray
    private lateinit var mTo: ByteArray

    /**
     * If this range is a proper subnet returns its prefix, otherwise returns null.
     */
    private var prefix: Int? = null

    /**
     * Determine if the range is a proper subnet and, if so, what the network prefix is.
     */
    private fun determinePrefix() {
        var matching = true
        prefix = mFrom.size * 8
        for (i in mFrom.indices) {
            for (bit in 0..7) {
                if (matching) {
                    if (mFrom[i].toInt() and
                        mBitmask[bit].toInt() != mTo[i].toInt() and
                        mBitmask[bit].toInt()
                    ) {
                        prefix = i * 8 + bit
                        matching = false
                    }
                } else {
                    if (mFrom[i].toInt() and
                        mBitmask[bit].toInt() != 0 ||
                        mTo[i].toInt() and
                        mBitmask[bit].toInt() == 0
                    ) {
                        prefix = null
                        return
                    }
                }
            }
        }
    }

    private constructor(from: ByteArray, to: ByteArray) {
        mFrom = from
        mTo = to
        determinePrefix()
    }

    constructor(from: InetAddress, to: InetAddress) {
        initializeFromRange(from, to)
    }

    private fun initializeFromRange(from: InetAddress, to: InetAddress) {
        val fa = from.address
        val ta = to.address
        require(fa.size == ta.size) { "Invalid range" }
        if (compareAddr(fa, ta) < 0) {
            mFrom = fa
            mTo = ta
        } else {
            mTo = fa
            mFrom = ta
        }
        determinePrefix()
    }

    constructor(base: InetAddress, prefix: Int) : this(base.address, prefix)
    private constructor(from: ByteArray, prefix: Int) {
        initializeFromCIDR(from, prefix)
    }

    private fun initializeFromCIDR(from: ByteArray, prefix: Int) {
        require(!(from.size != 4 && from.size != 16)) { "Invalid address" }
        require(!(prefix < 0 || prefix > from.size * 8)) { "Invalid prefix" }
        val to = from.clone()
        val mask = (0xff shl 8 - prefix % 8).toByte()
        val i = prefix / 8
        if (i < from.size) {
            from[i] = (from[i].toInt() and mask.toInt()).toByte()
            to[i] = (to[i].toInt() or mask.inv().toInt()).toByte()
            Arrays.fill(from, i + 1, from.size, 0.toByte())
            Arrays.fill(to, i + 1, to.size, 0xff.toByte())
        }
        mFrom = from
        mTo = to
        this.prefix = prefix
    }

    constructor(cidr: String) {
        /* only verify the basic structure */
        val regex = "(?i)^(([0-9.]+)|([0-9a-f:]+))(-(([0-9.]+)|([0-9a-f:]+))|(/\\d+))?$".toRegex()
        require(cidr.matches(regex)) {
            "Invalid CIDR or range notation"
        }
        if (cidr.contains("-")) {
            val parts = cidr.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val from = InetAddress.getByName(parts[0])
            val to = InetAddress.getByName(parts[1])
            initializeFromRange(from, to)
        } else {
            val parts = cidr.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val addr = InetAddress.getByName(parts[0])
            val base = addr.address
            var prefix = base.size * 8
            if (parts.size > 1) {
                prefix = parts[1].toInt()
            }
            initializeFromCIDR(base, prefix)
        }
    }

    val from: InetAddress?
        /**
         * Returns the first address of the range. The network ID in case this is a proper subnet.
         */
        get() = try {
            InetAddress.getByAddress(mFrom)
        } catch (ignored: UnknownHostException) {
            null
        }
    val to: InetAddress?
        /**
         * Returns the last address of the range.
         */
        get() {
            return try {
                InetAddress.getByAddress(mTo)
            } catch (ignored: UnknownHostException) {
                null
            }
        }

    override operator fun compareTo(other: IPRange): Int {
        var cmp = compareAddr(mFrom, other.mFrom)
        if (cmp == 0) {    /* smaller ranges first */
            cmp = compareAddr(mTo, other.mTo)
        }
        return cmp
    }

    override fun equals(o: Any?): Boolean {
        return if (o == null || o !is IPRange) {
            false
        } else this === o || compareTo(o) == 0
    }

    override fun toString(): String {
        return try {
            if (prefix != null) {
                InetAddress.getByAddress(mFrom).hostAddress + "/" + prefix
            } else (InetAddress.getByAddress(mFrom).hostAddress + "-" +
                    InetAddress.getByAddress(mTo).hostAddress)
        } catch (ignored: UnknownHostException) {
            super.toString()
        }
    }

    private fun compareAddr(a: ByteArray, b: ByteArray): Int {
        if (a.size != b.size) {
            return if (a.size < b.size) -1 else 1
        }
        for (i in a.indices) {
            if (a[i] != b[i]) {
                return if ((a.get(i).toInt() and 0xff) < (b.get(i).toInt() and 0xff)) {
                    -1
                } else {
                    1
                }
            }
        }
        return 0
    }

    /**
     * Check if this range fully contains the given range.
     */
    operator fun contains(range: IPRange): Boolean {
        return compareAddr(mFrom, range.mFrom) <= 0 && compareAddr(range.mTo, mTo) <= 0
    }

    /**
     * Check if this and the given range overlap.
     */
    fun overlaps(range: IPRange): Boolean {
        return !(compareAddr(mTo, range.mFrom) < 0 || compareAddr(range.mTo, mFrom) < 0)
    }

    private fun dec(addr: ByteArray): ByteArray {
        for (i in addr.indices.reversed()) {
            if (--addr[i] != 0xff.toByte()) {
                break
            }
        }
        return addr
    }

    private fun inc(addr: ByteArray): ByteArray {
        for (i in addr.indices.reversed()) {
            if ((++addr[i]).toInt() != 0) {
                break
            }
        }
        return addr
    }

    /**
     * Remove the given range from the current range.  Returns a list of resulting ranges (these are
     * not proper subnets). At most two ranges are returned, in case the given range is contained in
     * this but does not equal it, which would result in an empty list (which is also the case if
     * this range is fully contained in the given range).
     */
    fun remove(range: IPRange): List<IPRange> {
        val list = ArrayList<IPRange>()
        if (!overlaps(range)) {    /*           | this | or | this |
         * | range |                     | range | */
            list.add(this)
        } else if (!range.contains(this)) {
            /* we are not completely removed, so none of these cases applies:
         * | this  | or  | this  |   or   | this  |
         * | range |     | range   |    |   range | */
            if (compareAddr(mFrom, range.mFrom) < 0 && compareAddr(range.mTo, mTo) < 0) {
                /* the removed range is completely within our boundaries:
             * |    this    |
             *   | range |   */
                list.add(IPRange(mFrom, dec(range.mFrom.clone())))
                list.add(IPRange(inc(range.mTo.clone()), mTo))
            } else {    /* one end is within our boundaries the other at or outside it:
             * | this     | or    | this     | or | this    |  or  | this    |
             * | range |       | range |            | range |           | range | */
                val from =
                    if (compareAddr(mFrom, range.mFrom) < 0) mFrom else inc(range.mTo.clone())
                val to = if (compareAddr(mTo, range.mTo) > 0) mTo else dec(range.mFrom.clone())
                list.add(IPRange(from, to))
            }
        }
        return list
    }

    private fun adjacent(range: IPRange): Boolean {
        if (compareAddr(mTo, range.mFrom) < 0) {
            val to = inc(mTo.clone())
            return compareAddr(to, range.mFrom) == 0
        }
        val from = dec(mFrom.clone())
        return compareAddr(from, range.mTo) == 0
    }

    /**
     * Merge two adjacent or overlapping ranges, returns null if it's not possible to merge them.
     */
    fun merge(range: IPRange): IPRange? {
        if (overlaps(range)) {
            if (contains(range)) {
                return this
            } else if (range.contains(this)) {
                return range
            }
        } else if (!adjacent(range)) {
            return null
        }
        val from = if (compareAddr(mFrom, range.mFrom) < 0) mFrom else range.mFrom
        val to = if (compareAddr(mTo, range.mTo) > 0) mTo else range.mTo
        return IPRange(from, to)
    }

    /**
     * Split the given range into a sorted list of proper subnets.
     */
    fun toSubnets(): List<IPRange> {
        val list = ArrayList<IPRange>()
        if (prefix != null) {
            list.add(this)
        } else {
            var i = 0
            var bit = 0
            val prefix: Int
            var netmask: Int
            val common_byte: Int
            val common_bit: Int
            var from_cur: Int
            var from_prev = 0
            var to_cur: Int
            var to_prev = 1
            var from_full = true
            var to_full = true
            val from = mFrom.clone()
            val to = mTo.clone()

            /* find a common prefix */while (i < from.size && from[i].toInt() and
                mBitmask[bit].toInt() == to[i].toInt() and mBitmask[bit].toInt()
            ) {
                if (++bit == 8) {
                    bit = 0
                    i++
                }
            }
            prefix = i * 8 + bit

            /* at this point we know that the addresses are either equal, or that the
             * current bits in the 'from' and 'to' addresses are 0 and 1, respectively.
             * we now look at the rest of the bits as two binary trees (0=left, 1=right)
             * where 'from' and 'to' are both leaf nodes.  all leaf nodes between these
             * nodes are addresses contained in the range.  to collect them as subnets
             * we follow the trees from both leaf nodes to their root node and record
             * all complete subtrees (right for from, left for to) we come across as
             * subnets.  in that process host bits are zeroed out.  if both addresses
             * are equal we won't enter the loop below.
             *      0_____|_____1       for the 'from' address we assume we start on a
             *   0__|__ 1    0__|__1    left subtree (0) and follow the left edges until
             *  _|_   _|_   _|_   _|_   we reach the root of this subtree, which is
             * |   | |   | |   | |   |  either the root of this whole 'from'-subtree
             * 0   1 0   1 0   1 0   1  (causing us to leave the loop) or the root node
             * of the right subtree (1) of another node (which actually could be the
             * leaf node we start from).  that whole subtree gets recorded as subnet.
             * next we follow the right edges to the root of that subtree which again is
             * either the 'from'-root or the root node in the left subtree (0) of
             * another node.  the complete right subtree of that node is the next subnet
             * we record.  from there we assume that we are in that right subtree and
             * recursively follow right edges to its root.  for the 'to' address the
             * procedure is exactly the same but with left and right reversed.
             */if (++bit == 8) {
                bit = 0
                i++
            }
            common_byte = i
            common_bit = bit
            netmask = from.size * 8
            i = from.size - 1
            while (i >= common_byte) {
                val bit_min = if (i == common_byte) common_bit else 0
                bit = 7
                while (bit >= bit_min) {
                    val mask = mBitmask[bit]
                    from_cur = from[i].toInt() and mask.toInt()
                    if (from_prev == 0 && from_cur != 0) {
                        /* 0 -> 1: subnet is the whole current (right) subtree */
                        list.add(IPRange(from.clone(), netmask))
                        from_full = false
                    } else if (from_prev != 0 && from_cur == 0) {
                        /* 1 -> 0: invert bit to switch to right subtree and add it */
                        from[i] = (from[i].toInt() xor mask.toInt()).toByte()
                        list.add(IPRange(from.clone(), netmask))
                        from_cur = 1
                    }
                    /* clear the current bit */from[i] =
                        (from[i].toInt() and mask.inv().toInt()).toByte()
                    from_prev = from_cur
                    to_cur = to[i].toInt() and mask.toInt()
                    if (to_prev != 0 && to_cur == 0) {
                        /* 1 -> 0: subnet is the whole current (left) subtree */
                        list.add(IPRange(to.clone(), netmask))
                        to_full = false
                    } else if (to_prev == 0 && to_cur != 0) {
                        /* 0 -> 1: invert bit to switch to left subtree and add it */
                        to[i] = (to[i].toInt() xor mask.toInt()).toByte()
                        list.add(IPRange(to.clone(), netmask))
                        to_cur = 0
                    }
                    /* clear the current bit */to[i] =
                        (to[i].toInt() and mask.inv().toInt()).toByte()
                    to_prev = to_cur
                    netmask--
                    bit--
                }
                i--
            }
            if (from_full && to_full) {
                /* full subnet (from=to or from=0.. and to=1.. after common prefix) - not reachable
             * due to the shortcut at the top */
                list.add(IPRange(from.clone(), prefix))
            } else if (from_full) {    /* full from subnet (from=0.. after prefix) */
                list.add(IPRange(from.clone(), prefix + 1))
            } else if (to_full) {    /* full to subnet (to=1.. after prefix) */
                list.add(IPRange(to.clone(), prefix + 1))
            }
        }
        list.sort()
        return list
    }
}

