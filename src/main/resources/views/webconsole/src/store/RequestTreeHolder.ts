import { defineStore } from "pinia";

export const RequestTreeHolder = defineStore("UserRequestHolder", {
  state: () => ({
    //当前选中的请求ID 如果选择的是组，则activeRequestId为null
    activeRequestId: null as string | null,

    //当前选中的组ID 如果选择的是请求，则activeGroupId为null
    activeGroupId: null as string | null,

    //当前选中的节点ID
    activeNodeId: null as string | null,

    //当前选中的tree类型
    activeNodeType: null as "group" | "request" | null,

    //当前展开的节点ID
    expandedNodeIds: [] as string[],
  }),

  getters: {
    getExpandedNodeIds: (state) => {
      //如果activeNodeId为空，则从localStorage中加载
      if (state.activeNodeId == null) {
        const expandedNodeIds = localStorage.getItem("request_tree_expanded_node_ids");
        if (expandedNodeIds) {
          state.expandedNodeIds = expandedNodeIds.split(",");
        }
      }

      return state.expandedNodeIds;
    },

    getActiveNodeId: (state) => {
      // 如果requestId为空，则从localStorage中加载
      if (state.activeNodeId == null) {
        console.log("LOAD FROM requestId", localStorage.getItem("request_tree_selected_request_id"));
        RequestTreeHolder().setActiveNodeId(localStorage.getItem("request_tree_selected_request_id") || null);
      }
      return state.activeNodeId;
    },

    getActiveNodeType: (state) => {
      //如果activeNodeType为空，则从localStorage中加载
      if (state.activeNodeType == null) {
        const activeNodeType = localStorage.getItem("request_tree_active_node_type") as "group" | "request" | null;
        if (activeNodeType) {
          state.activeNodeType = activeNodeType;
        }
      }
      return state.activeNodeType;
    },

    getActiveRequestId: (state) => {
      //如果activeRequestId为空，则从localStorage中加载
      if (state.activeRequestId == null) {
        const activeRequestId = localStorage.getItem("request_tree_active_request_id") as string | null;
        if (activeRequestId) {
          state.activeRequestId = activeRequestId;
        }
      }
      return state.activeRequestId;
    },

    getActiveGroupId: (state) => {
      //如果activeGroupId为空，则从localStorage中加载
      if (state.activeGroupId == null) {
        const activeGroupId = localStorage.getItem("request_tree_active_group_id") as string | null;
        if (activeGroupId) {
          state.activeGroupId = activeGroupId;
        }
      }
      return state.activeGroupId;
    },
  },

  actions: {
    setActiveNodeId(activeNodeId: string | null) {
      if (activeNodeId == null) {
        localStorage.removeItem("request_tree_selected_request_id");
        this.activeNodeId = null;
        return;
      }

      if (this.activeNodeId != activeNodeId) {
        localStorage.setItem("request_tree_selected_request_id", activeNodeId);
      }
      this.activeNodeId = activeNodeId;
    },

    setExpandedNodeIds(expandedNodeIds: string[] | null) {
      //如果activeNodeIds为空，则从localStorage中删除
      if (expandedNodeIds == null) {
        localStorage.removeItem("request_tree_expanded_node_ids");
        this.expandedNodeIds = [];
        return;
      }

      localStorage.setItem("request_tree_expanded_node_ids", expandedNodeIds.join(","));
      this.expandedNodeIds = expandedNodeIds;
    },

    setActiveNodeType(nodeType: "group" | "request" | null) {
      //如果activeNodeType为空，则从localStorage删除
      if (nodeType == null) {
        localStorage.removeItem("request_tree_active_node_type");
        this.activeNodeType = null;
        return;
      }

      localStorage.setItem("request_tree_active_node_type", nodeType);
      this.activeNodeType = nodeType;
    },

    setActiveRequestId(requestId: string | null) {
      //如果requestId为空，则从localStorage中删除
      if (requestId == null) {
        localStorage.removeItem("request_tree_active_request_id");
        this.activeRequestId = null;
        return;
      }

      localStorage.setItem("request_tree_active_request_id", requestId);
      this.activeRequestId = requestId;
    },

    setActiveGroupId(groupId: string | null) {
      //如果groupId为空，则从localStorage中删除
      if (groupId == null) {
        localStorage.removeItem("request_tree_active_group_id");
        this.activeGroupId = null;
        return;
      }

      localStorage.setItem("request_tree_active_group_id", groupId);
      this.activeGroupId = groupId;
    },
  },
});
