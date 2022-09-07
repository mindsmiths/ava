import Vue from "vue";
import Vuex from "vuex";
import Buefy from "buefy";
import "es6-promise/auto";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";

import "armory-sdk/src/assets/css/main.css";
import "armory-sdk/src/assets/css/override.scss";
import "armory-sdk/src/assets/css/default-skin.scss";
import "./assets/css/skin.scss";

import baseStore from "armory-sdk/src/store";

import App from "./App.vue";

Vue.config.productionTip = false;
Vue.use(Vuex);
Vue.use(Buefy);
Vue.component("font-awesome-icon", FontAwesomeIcon);
library.add(faArrowLeft);

const store = new Vuex.Store(baseStore);
new Vue({
  render: (h) => h(App),
  store,
}).$mount("#app");
