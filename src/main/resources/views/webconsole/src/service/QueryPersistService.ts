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
            if(query[key]) {
                localStorage.setItem(prefix + '_' + key, query[key] + '')
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
            if(value) {

                if(typeof query[key] === 'number') {
                    query[key] = parseInt(value)
                    continue
                }

                if(typeof query[key] === 'string') {
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
            if(localStorage.key(i)?.startsWith(prefix+'_')) {
                localStorage.removeItem(localStorage.key(i)!)
            }
        }
    }   
    
    

}