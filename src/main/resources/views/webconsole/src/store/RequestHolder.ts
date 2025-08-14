import { defineStore } from "pinia";

export const UserRequestHolder = defineStore('UserRequestHolder', {


    state: () => ({

        //当前选中的请求ID
        requestId: null as string | null,

        //当前展开的节点ID
        activeNodeId: [] as string[]

    }),

    getters: {

        getActiveNodeIds: (state) => {

            //如果activeNodeId为空，则从localStorage中加载
            if(state.activeNodeId.length == 0){
                const activeNodeIds = localStorage.getItem('request_tree_active_node_ids')
                if(activeNodeIds){
                    state.activeNodeId = activeNodeIds.split(',')
                }
            }

            return state.activeNodeId
        }, 

        getRequestId: (state) => {
            // 如果requestId为空，则从localStorage中加载
            if(state.requestId == null){
                console.log('LOAD FROM requestId', localStorage.getItem('request_tree_selected_request_id'))
                UserRequestHolder().setRequestId(localStorage.getItem('request_tree_selected_request_id') || null)
            } 
            return state.requestId
        }
    },

    actions: {

        setRequestId(requestId: string | null) {
            if(requestId == null){
                localStorage.removeItem('request_tree_selected_request_id')
                this.requestId = null
                return
            }

            if(this.requestId != requestId){
                localStorage.setItem('request_tree_selected_request_id', requestId)
            }
            this.requestId = requestId
        },

        setActiveNodeIds(activeNodeIds: string[] | null) {

            //如果activeNodeIds为空，则从localStorage中删除
            if(activeNodeIds == null){
                localStorage.removeItem('request_tree_active_node_ids')
                this.activeNodeId = []
                return
            }

            localStorage.setItem('request_tree_active_node_ids', activeNodeIds.join(','))
            this.activeNodeId = activeNodeIds
        }
        

    }
})


