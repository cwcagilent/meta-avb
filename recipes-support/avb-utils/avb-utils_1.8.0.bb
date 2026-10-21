SUMMARY = "A toolkit that brings Android Verified Boot (AVB) to embedded Linux"
HOMEPAGE = "https://github.com/embetrix/avb-utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=570a9b3749dd0463a1778803b12a6dce"

# The target build needs none of the submodules: libavb comes from the libavb
# recipe and avbtool from python3-avbtool. The native build also produces fec,
# which is assembled from six AOSP repositories that are submodules under fec/,
# so it fetches them.
SRC_URI = " \
    git://github.com/cwcagilent/avb-utils.git;name=avb-utils;protocol=https;branch=refactor-fec \
    "
SRC_URI:class-native = " \
    gitsm://github.com/cwcagilent/avb-utils.git;name=avb-utils;protocol=https;branch=refactor-fec \
    "
# 1.8.0 (d1fa2933) plus the FEC series and the in-tree fec build, from the
# fork carrying them until upstream takes the series. Not yet tagged. The
# branch is refactor-fec until that work is merged to master.
SRCREV = "8dc296049c25d87bcd8e384a8f20d46c6329c89b"

DEPENDS += "libavb python3-avbtool-native"
# For fec: libavb is built in statically from the submodule, but the
# Reed-Solomon and sparse-image code need these.
DEPENDS:append:class-native = " openssl-native zlib-native"

# fec brings in Apache-2.0 code from AOSP and Phil Karn's LGPL Reed-Solomon
# library. Native-only, so nothing of it reaches an image.
LICENSE:class-native = "GPL-2.0-only & Apache-2.0 & LGPL-2.1-or-later"
LIC_FILES_CHKSUM:append:class-native = " \
    file://fec/extras/libfec/NOTICE;md5=b433a746dd6fe8862028b1d7fc412a4d \
    file://fec/fec_rs/NOTICE;md5=0448d3676bc0de00406af227d341a4d1 \
    file://avb/LICENSE;md5=b8228f2369d92593f53f0a0685ebd3c0 \
"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-python"

# INSTALL_AVB_SIGN also turns on BUILD_FEC: avbtool shells out to fec for the
# dm-verity parity avb_sign always writes.
EXTRA_OECMAKE:class-native = "-DINSTALL_AVB_SIGN=ON"

# Prove fec works rather than only that it linked, with the repository's own
# round-trip test: encode, corrupt, repair, compare.
addtask check_fec after do_compile before do_install
do_check_fec() {
    :
}
do_check_fec:class-native() {
    sh ${S}/tests/fec_roundtrip.sh ${B}/fec/fec || bbfatal "fec could not repair a corrupted image"
}

# INSTALL_AVB_SIGN also installs the submodule's avbtool.py as avbtool when the
# submodule is present, which the gitsm fetch makes it. python3-avbtool-native
# already stages ${bindir}/avbtool, from the same AVB revision, and two
# providers of one path break every recipe that sysroots both.
do_install:append:class-native() {
    rm -f ${D}${bindir}/avbtool
}

RDEPENDS:${PN}:class-target = "avb-keys"
RDEPENDS:${PN}-python = "python3-core python3-avbtool openssl"

FILES:${PN}-python = "${bindir}/avb_sign"

BBCLASSEXTEND = "native nativesdk"
