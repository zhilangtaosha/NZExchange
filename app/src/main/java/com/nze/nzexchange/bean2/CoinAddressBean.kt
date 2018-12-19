package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/19
 */
class CoinAddressBean(var coinName: String, var coinAddress: MutableList<AddressBean>) {

    companion object {
        fun getList() = mutableListOf<CoinAddressBean>().apply {

            add(CoinAddressBean("ETH", mutableListOf<AddressBean>().apply {
                add(AddressBean("办公室提币地址", "sdfsdf46512"))
                add(AddressBean("家里提币地址", "sdfsdf46512"))
            }))
            add(CoinAddressBean("USDT", mutableListOf<AddressBean>().apply {
                add(AddressBean("办公室提币地址", "asdf454"))
                add(AddressBean("家里提币地址", "651sadfsdf"))
            }))
        }
    }


}

class AddressBean(var desc: String, var address: String)
