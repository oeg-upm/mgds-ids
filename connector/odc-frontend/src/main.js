import Vue from 'vue'
import axios from './plugins/axios'
import App from './App.vue'
import vueNcform from '@ncform/ncform';
import Element from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import ncformStdComps from '@ncform/ncform-theme-elementui';
import Vuetify from "vuetify";
import "vuetify/dist/vuetify.css";
import VueProgressBar from 'vue-progressbar';
import 'material-design-icons-iconfont/dist/material-design-icons.css' // Ensure you are using css-loader
import RuntimeConfiguration from './runtimeconfig';
import { glueConfig as GLUE_CONFIG } from '../config/user-config';

Vue.use(Vuetify);

Vue.config.productionTip = false;

import "@/assets/stylesheets/main.css"
import "@/assets/stylesheets/darkly.bootstrap.css"

import router from './router'
import store from './store'
Vue.config.devtools = true;
router.afterEach((to, from) => {
  store.commit('delete');
});

// Vue-progressbar setup
const progressBarOptions = {
  thickness: '5px',
  autoRevert: false,
  transition: {
    speed: '1.0s',
    opacity: '0.5s',
    termination: 1000,
  },
};

Vue.use(VueProgressBar, progressBarOptions);
Vue.use(Element);
Vue.use(RuntimeConfiguration, { baseConfig: GLUE_CONFIG, debug: true });

Vue.use(vueNcform, { extComponents: ncformStdComps, /*lang: 'zh-cn'*/ });
window.$http = Vue.prototype.$http = axios;
export default new Vue({
  router,
  vuetify : new Vuetify({

    icons: {
      iconfont: 'md',
    }
  }),
  store,
  axios,
  render: h => h(App)
}).$mount('#app')
