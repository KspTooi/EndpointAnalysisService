import type { Reactive } from "vue"

export default{

    /**
     * 持久化查询参数到localStorage
     * @param prefix 持久化前缀
     * @param query 查询参数对象
     * @returns void
     */
    persistQuery(prefix: string, query: Reactive<any>):void {
        for(let key in query) {
            const value = query[key]
            if(value !== null && value !== undefined && value !== '') {
                localStorage.setItem(prefix + '_' + key, value + '')
            } else {
                localStorage.removeItem(prefix + '_' + key)
            }
        }
    },
    
    /**
     * 从localStorage加载已保存的查询参数
     * @param prefix 持久化前缀
     * @param query 查询参数对象
     * @returns void
     */
    loadQuery(prefix: string, query: Reactive<any>):void {
        for(let key in query) {
            const value = localStorage.getItem(prefix + '_' + key)
            if(value !== null) {

                if(typeof query[key] === 'number') {
                    query[key] = parseInt(value)
                    continue
                }

                if(typeof query[key] === 'string' || query[key] === null) {
                    query[key] = value
                    continue
                }

                query[key] = value
            }
        }
    },

    /**
     * 清除已保存的查询参数
     * @param prefix 持久化前缀
     * @returns void
     */
    clearQuery(prefix: string):void {
        for(let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i)
            if(key?.startsWith(prefix+'_')) {
                localStorage.removeItem(key)
                i-- // 因为移除了一个元素，所以索引需要减一
            }
        }
    }   
    
    

}