import { defineStore } from "pinia";

export const ReloadHolder = defineStore('ReloadHolder', {

    state: () => ({
        //是否需要重新加载树
        needReloadTree: 0
    }),

    getters: {
        isNeedReloadTree: (state) => {
            return state.needReloadTree
        }
    },

    actions: {
        requestReloadTree() {
            this.needReloadTree++
        }
    }
})
