SUMMARY = "A toolkit that brings Android Verified Boot (AVB) to embedded Linux"
HOMEPAGE = "https://github.com/embetrix/avb-utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=570a9b3749dd0463a1778803b12a6dce"

SRC_URI = " \
    git://github.com/embetrix/avb-utils.git;name=avb-utils;protocol=https;branch=master \
    "
# tag: 1.8.0
SRCREV = "4e75d749df4878acbe959b8b6b106bd894e75bcd"

DEPENDS += "libavb python3-avbtool-native"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-python"

EXTRA_OECMAKE:class-native = "-DINSTALL_AVB_SIGN=ON"

RDEPENDS:${PN}:class-target = "avb-keys"
RDEPENDS:${PN}-python = "python3-core python3-avbtool openssl"

FILES:${PN}-python = "${bindir}/avb_sign"

BBCLASSEXTEND = "native nativesdk"
