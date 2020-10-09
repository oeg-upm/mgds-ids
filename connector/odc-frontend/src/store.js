import Vue from 'vue'
import Vuex from 'vuex'
import Toasted from 'vue-toasted'
Vue.use(Toasted)
Vue.use(Vuex)

let auth = {
  headers: {
    Authorization:{
      toString () {
        return   'Bearer ' + localStorage.getItem('jwt')
      }
    }
  }
}

export default new Vuex.Store({
  state: {
    status:null,
    text:null,
    sources:[]
  },
  mutations: {
    update(state, newInfo){
      state.status = newInfo.status
      state.text = newInfo.text
    },
    delete(state){
      state.status = null
      state.text = null
    },
    SAVE_SOURCES(state, sources) {
      state.sources = sources;
    }
  },
  getters:{
    getStatus: state =>  {
      return state.status
    },
    getText: state => {
      return state.text
    },
    getSources:state => {
      return state.sources
    }
  },
  actions: {
    update(context, newInfo){
      context.commit('update', newInfo)
      if (context.getters.getStatus == 'success'){
        Vue.toasted.success(context.getters.getText, {"position": "top-center","duration": "1500"})
      }
      else if(context.getters.getStatus == 'info'){
        Vue.toasted.info(context.getters.getText, {"position": "top-center","duration": "1500"})      
      }          
      else if(context.getters.getStatus == 'error'){
        Vue.toasted.error(context.getters.getText, {"position": "top-center","duration": "1500"})
      }
    },
    loadSources:({ commit }, config) =>{
      Vue.axios.get(config.apiBase+'/api/listAdapters',auth).then(result => {
        let adpts = []
        let adapterNames =[]
        for( var i in result.data.sort() ){
          adapterNames.push(result.data[i].name)
        }
        adapterNames.push("File Upload")
        for( var j in adapterNames){
          Vue.axios.get(config.apiBase+'/api/datasources/find/type/'+adapterNames[j],auth).then(adapt => {
            adpts.push({
              type: adapt.data.type,
              sources: adapt.data.result
            })
          });
        }
        commit('SAVE_SOURCES', adpts);
      }).catch(error => {
        throw new Error(`API ${error}`);
      });

    }
  }
})
