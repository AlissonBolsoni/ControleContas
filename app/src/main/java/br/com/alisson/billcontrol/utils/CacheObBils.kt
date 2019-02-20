package br.com.alisson.billcontrol.utils

import br.com.alisson.billcontrol.data.models.ObBill

object CacheObBils {

    private var cacheMap: HashMap<String, Pair<ArrayList<ObBill>, Float>>? = null

    private fun initCache() {
        if (cacheMap == null)
            cacheMap = HashMap()
        else
            cacheMap!!.clear()
    }

    fun set(bills: ArrayList<ObBill>) {
        initCache()
        for (bill in bills) {
            val key = DateUtils.getCacheKey(bill.expirationDate)
            val list = cacheMap!![key]
            if (list == null) {
                val temp = ArrayList<ObBill>()
                temp.add(bill)
                cacheMap!![key] = Pair(temp, bill.billValue)
            } else {
                val temp = cacheMap!![key]!!
                temp.first.add(bill)
                cacheMap!![key] = Pair(temp.first, temp.second + bill.billValue)
            }
        }
    }

    fun get(key: String): ArrayList<ObBill> {
        cacheMap ?: return ArrayList()
        return cacheMap!![key]?.first ?: return ArrayList()
    }

    fun getKeys() = ArrayList(cacheMap!!.keys)

    fun getValue(key: String): Float {
        return cacheMap!![key]!!.second
    }
}