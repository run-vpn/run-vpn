package com.runvpn.app.android.utils

import java.util.TreeSet


class IPRangeSet : Iterable<IPRange> {
    private val mRanges: TreeSet<IPRange> = TreeSet<IPRange>()

    /**
     * Add a range to this set. Automatically gets merged with existing ranges.
     */
    fun add(range: IPRange) {
        var range: IPRange = range
        if (mRanges.contains(range)) {
            return
        }
        reinsert@ while (true) {
            val iterator: MutableIterator<IPRange> = mRanges.iterator()
            while (iterator.hasNext()) {
                val existing: IPRange = iterator.next()
                val replacement: IPRange? = existing.merge(range)
                if (replacement != null) {
                    iterator.remove()
                    range = replacement
                    continue@reinsert
                }
            }
            mRanges.add(range)
            break
        }
    }

    /**
     * Add all ranges from the given set.
     */
    fun add(ranges: IPRangeSet) {
        if (ranges === this) {
            return
        }
        for (range in ranges.mRanges) {
            add(range)
        }
    }

    /**
     * Add all ranges from the given collection to this set.
     */
    fun addAll(coll: Collection<IPRange>) {
        for (range in coll) {
            add(range)
        }
    }

    /**
     * Remove the given range from this set. Existing ranges are automatically adjusted.
     */
    fun remove(range: IPRange) {
        val additions: ArrayList<IPRange> = ArrayList()
        val iterator: MutableIterator<IPRange> = mRanges.iterator()
        while (iterator.hasNext()) {
            val existing: IPRange = iterator.next()
            val result: List<IPRange> = existing.remove(range)
            if (result.isEmpty()) {
                iterator.remove()
            } else if (result[0] != existing) {
                iterator.remove()
                additions.addAll(result)
            }
        }
        mRanges.addAll(additions)
    }

    /**
     * Remove the given ranges from ranges in this set.
     */
    fun remove(ranges: IPRangeSet) {
        if (ranges === this) {
            mRanges.clear()
            return
        }
        for (range in ranges.mRanges) {
            remove(range)
        }
    }

    /**
     * Get all the subnets derived from all the ranges in this set.
     */
    fun subnets(): Iterable<IPRange> {
        return object : Iterable<IPRange> {
            override fun iterator(): Iterator<IPRange> {
                return object : MutableIterator<IPRange> {
                    private val mIterator: Iterator<IPRange> = mRanges.iterator()
                    private var mSubnets = ArrayList<IPRange>()
                    override fun hasNext(): Boolean {
                        return mSubnets != null && mSubnets.size > 0 || mIterator.hasNext()
                    }

                    override fun next(): IPRange {
                        if (mSubnets == null || mSubnets.size == 0) {
                            val range: IPRange = mIterator.next()
                            mSubnets.clear()
                            mSubnets.addAll(mRanges)
                        }
                        return mSubnets.removeAt(0)
                    }

                    override fun remove() {
                        throw UnsupportedOperationException()
                    }
                }
            }
        }
    }

    override fun iterator(): Iterator<IPRange> {
        return mRanges.iterator()
    }

    /**
     * Returns the number of ranges, not subnets.
     */
    fun size(): Int {
        return mRanges.size
    }

    override fun toString(): String {
        /* we could use TextUtils, but that causes the unit tests to fail */
        val sb = StringBuilder()
        for (range in mRanges) {
            if (sb.length > 0) {
                sb.append(" ")
            }
            sb.append(range.toString())
        }
        return sb.toString()
    }

    companion object {
        /**
         * Parse the given string (space separated ranges in CIDR or range notation) and return the
         * resulting set or `null` if the string was invalid. An empty set is returned
         * if the given string is `null`.
         */
        fun fromString(ranges: String?): IPRangeSet {
            val set = IPRangeSet()
            if (ranges != null) {
                for (range in ranges.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()) {
                    try {
                        set.add(IPRange(range))
                    } catch (unused: Exception) {
                        /* besides due to invalid strings exceptions might get thrown if the string
                 * contains a hostname (NetworkOnMainThreadException) */
                        return IPRangeSet()
                    }
                }
            }
            return set
        }
    }
}

