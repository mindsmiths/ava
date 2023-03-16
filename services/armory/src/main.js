import Vue from "vue";
import Vuex from "vuex";
import Buefy from "buefy";
import "es6-promise/auto";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
  faArrowLeft,
  faPlus,
  faCheck,
  faCheckCircle,
} from "@fortawesome/free-solid-svg-icons";
import * as Sentry from "@sentry/vue";

import "armory-sdk/src/assets/css/main.css";
import "./assets/css/skin.scss";

import baseStore from "armory-sdk/src/store";

import App from "./App.vue";

Vue.config.productionTip = false;
Vue.use(Vuex);
Vue.component("font-awesome-icon", FontAwesomeIcon);
library.add(faArrowLeft, faPlus, faCheck, faCheckCircle);
Vue.use(Buefy, {
  defaultIconComponent: "font-awesome-icon",
  defaultIconPack: "fas",
});

if (window.sentryDsn)
  Sentry.init({
    Vue,
    dsn: window.sentryDsn,
    tracesSampleRate: 1.0,
  });

const store = new Vuex.Store(baseStore);
new Vue({
  render: (h) => h(App),
  store,
}).$mount("#app");
