<script>
import Armory from "armory-sdk/src/Armory";

export default {
  name: "App",
  extends: Armory,
  methods: {
    getCustomTemplates() {
      return {};
    },
    getCustomComponents() {
      return {};
    },
    // eslint-disable-next-line no-unused-vars
    preReceivedSpecificationTrigger(receivedData) {},
    postReceivedSpecificationTrigger() {},
  },
};
</script>
