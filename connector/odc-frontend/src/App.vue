<template>
  <div id="app">
      <Header></Header>
      <div class="container">
          <vue-progress-bar />
          <br />
          <div class="row">
              <div class="col-xl">
                  <router-view></router-view>
              </div>
          </div>
      </div>
    </div>
</template>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js" type="text/javascript"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js" type="text/javascript"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.min.js" type="text/javascript"></script>
        <script src="@/assets/javascripts/main.js" type="text/javascript"></script>
<script>
import Header from '@/components/Header.vue'

export default {
  name: 'app',
  components: {
    Header
  }
}
</script>