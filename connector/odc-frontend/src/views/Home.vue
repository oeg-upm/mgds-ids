<template>
<div class="row">
        <div class="col-lg-8">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Willkommen beim IDS Open Data Connector!</h5>
                    <p class="card-text">
                        Der Open Data Connector ermöglicht die einfache Bereitstellung von Open Data unter den Spezifikationen
                        des Industrial Data Spaces. Die Daten liegen dabei ursprünglich in der Open Source Datenmanagementsoftware CKAN vor.
                    </p>
                    <p>
                         Bitte aktualisieren Sie im ersten Schritt. Die Konfiguration des Konnektors.
                    </p>
                    <router-link to="/configuration" type="button" class="btn btn-info mt-2">Konfiguration aktualisieren</router-link>
                </div>
            </div>


          </div>
          <div class="col-lg-4">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Statistik</h5>
                    <p class="card-text">
                        {{counts.dacount}} Data Assets<br />
                        {{counts.publishedcount}} Veröffentlichte Data Assets
                    </p>
                </div>
            </div>
            <div class="card mt-4">
                <div class="card-body">
                    <h5 class="card-title">Konfiguration</h5>
                    <p class="card-text">
                        <router-link to="/configuration" type="button" class="btn btn-info">Konfiguration ändern</router-link> <br />
                        <router-link to="/apps" type="button" class="btn btn-info mt-2">Apps verwalten</router-link> <br />
                        <router-link to="/brokers" type="button" class="btn btn-info mt-2">Broker verwalten</router-link>
                    </p>
                </div>
            </div>
            <div class="card mt-4">
                <div class="card-body">
                    <h5 class="card-title">Data Assets verwalten</h5>
                    <p class="card-text">
                        <router-link to="/datasource/select" type="button" class="btn btn-info">Data Source hinzufügen</router-link> <br />
                        <router-link to="/dataasset" type="button" class="btn btn-info mt-2">Data Assets anzeigen</router-link>
                    </p>
                </div>
            </div>

        </div>
    </div>
</template>


<script>
export default {
  name: 'home',
  data() {
        return {
            counts:{
                'dacount':0,
                'publishedcount':0
            }
        };
  },
  mounted(){
      this.getDataAssetCounts()
  },
  components: {
    
  },
  methods:{
    getWindowLocation(){
      return window.location.href
    },
    getDataAssetCounts(){
        this.$axios({
            method: 'get',
            url: new URL('/api/dataassets/counts', this.$env.apiBaseUrl),
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('jwt')
            }
        })
        .then(response => {
            this.counts.dacount = response.data.dacount;
            this.counts.publishedcount = response.data.publishedcount;
            })
        .catch(error => {
            if(error.response.status === 401){
                this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                this.$router.push("/login")                            
            }
         })
    }
  }
}
</script>
