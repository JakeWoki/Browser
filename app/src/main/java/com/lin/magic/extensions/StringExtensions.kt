package com.lin.magic.extensions

val String.reverseDomainName get() = split('.').reversed().joinToString(".")