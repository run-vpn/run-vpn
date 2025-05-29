package com.runvpn.app.data.servers.utils

import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource

object TestData {


    val testServer1 = Server(
        uuid = "uuid1",
        name = null,
        host = "host server",
        protocol = ConnectionProtocol.XRAY,
        source = ServerSource.SERVICE,
        isFavorite = false,
        isPublic = false,

        //Nullable Fields
        country = "Country",
        city = "city",
        iso = "US",
        latitude = "latitude",
        longitude = "longitude",
        config = null,
    )
    val testServer2 = Server(
        uuid = "uuid2",
        name = null,
        host = "host server",
        protocol = ConnectionProtocol.XRAY,
        source = ServerSource.SERVICE,
        isFavorite = false,
        isPublic = false,

        //Nullable Fields
        country = "Country",
        city = "city",
        iso = "US",
        latitude = "latitude",
        longitude = "longitude",
        config = null,
    )
    val testServer3 = Server(
        uuid = "uuid3",
        name = null,
        host = "host server",
        protocol = ConnectionProtocol.XRAY,
        source = ServerSource.SERVICE,
        isFavorite = false,
        isPublic = false,

        //Nullable Fields
        country = "Country",
        city = "city",
        iso = "US",
        latitude = "latitude",
        longitude = "longitude",
        config = null,
    )

    val testServer4 = Server(
        uuid = "uuid4",
        name = null,
        host = "host server",
        protocol = ConnectionProtocol.XRAY,
        source = ServerSource.SERVICE,
        isFavorite = false,
        isPublic = false,

        //Nullable Fields
        country = "Country",
        city = "city",
        iso = "US",
        latitude = "latitude",
        longitude = "longitude",
        config = null,
    )
    val testServerList = listOf(
        testServer1,
        testServer2,
        testServer3,
        testServer4
    )

    val customServerList = listOf(
        testServer3,
        testServer4
    )

//    val testFavorTestList = listOf(
//        testFavorServer,
//        testFavorServer,
//        testFavorServer,
//        testFavorServer
//    )

}

