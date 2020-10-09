<template>
    <header>
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark" style="max-height: 60px;">
            <a class="navbar-brand" href=".">
                <div style="display: flex">
                    <img src="../assets/images/ids-white2.png" alt="Logo of Industrial data space" class="navbar-logo" style="width: auto; height: 50px;margin-right: 10px;">
                    <div class="boxhead">
                        <h2 style="font-size: 1.2rem;color: white;margin: auto;" >International</h2>
                        <h2 style="font-size: 1.2rem;color: white;margin: auto;" ><strong>Data Spaces</strong></h2></div>
                </div>
            </a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item">
                        <router-link to="/" class="nav-link">Home</router-link> 
                    </li>
                    <li class="nav-item">
                        <router-link to="/dataasset" class="nav-link">Data Assets</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link to="/datasource/select" class="nav-link">Hinzufügen</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link to="/job" class="nav-link">Jobs</router-link>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" :href="getUrl('/about')">Connector</a>
                    </li>
                    <li class="nav-item">
                       <router-link to="/apps" class="nav-link">Apps</router-link>
                    </li>
                    <li class="nav-item">
                       <router-link to="/brokers" class="nav-link">Brokers</router-link>
                    </li>
                    <li class="nav-item">
                       <router-link to="/configuration" class="nav-link">Konfiguration</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link to="/login" class="nav-link">Logout</router-link>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li class="nav-item">
                        <a class="nav-link" href="https://www.fokus.fraunhofer.de/imprint">Imprint</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="https://www.fokus.fraunhofer.de/data_protection">Data Protection</a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>
</template>
<script>
export default {
  name: 'header',
  data() {
        return {
            'baseUrl':this.$env.apiBaseUrl
        };
  },
  methods:{
      getUrl(path){
          return new URL(path, this.baseUrl)
      }
  }
}
</script>