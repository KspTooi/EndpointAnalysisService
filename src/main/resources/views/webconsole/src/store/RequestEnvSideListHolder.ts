import { defineStore } from "pinia";

export const RequestEnvSideListHolder = defineStore("RequestEnvSideListHolder", {
  state: () => ({
    selectedId: null as string | null,
  }),

  getters: {
    getSelectedId: (state) => {
      if (state.selectedId) {
        return state.selectedId;
      }
      const persistedSelectedId = localStorage.getItem("request_env_side_list_selected_id");
      if (persistedSelectedId) {
        state.selectedId = persistedSelectedId;
      }
      return state.selectedId;
    },
  },

  actions: {
    setSelectedId(id: string | null) {
      if (id === null) {
        localStorage.removeItem("request_env_side_list_selected_id");
      } else {
        localStorage.setItem("request_env_side_list_selected_id", id);
      }
      this.selectedId = id;
    },
  },
});
