<template>
    <div>
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Konfiguration des Open Data Konnektors</h5>
                <p class="card-text">
                    Konfigurieren Sie ihren Konnektor zur korrekten Darstellung des IDS Informationsmodells.
                </p>
                <div class="form-group">
                    <label for="connectorTitle">Konnektor Titel</label>
                    <input type="text"
                            class="form-control"
                            id="connectorTitle"
                            name="connectorTitle"
                            placeholder=""
                            v-model="configuration.title">
                </div>
                <div class="form-group">
                    <label for="connectorURL">Konnektor URL</label>
                    <input type="text"
                            class="form-control"
                            id="connectorURL"
                            name="connectorURL"
                            placeholder=""
                            v-model="configuration.url">
                </div>
                <div class="form-group">
                    <label for="connectorMaintainer">Maintainer</label>
                    <input type="text"
                            class="form-control"
                            id="connectorMaintainer"
                            name="connectorMaintainer"
                            placeholder=""
                            v-model="configuration.maintainer">
                </div>
                <div class="form-group">
                    <label for="connectorCurator">Curator</label>
                    <input type="text"
                            class="form-control"
                            id="connectorCurator"
                            name="connectorCurator"
                            placeholder=""
                            v-model="configuration.curator">
                </div>
                <div class="form-group">
                    <label for="connectorCountry">Konektorstandort</label>
                    <select id="connectorCountry" name="locality" class="form-control" v-model="geoname" :required="true">
                        <option  class="form-control" v-for="country in countries"
                                  >{{ country.countryName }}</option>
                    </select>
                </div>
                <button v-on:click="submit()" class="btn btn-primary">Konfiguration bearbeiten</button>
            </div>
        </div>
    </div>
</template>

<script>
    import geonames from '@/allCountriesWithGeonames.json'
export default {
    name:"Configuration",
    data() {
        return {
            countries:geonames.geonames,
            geoname:"Deutschland",
            configuration:{
                url:"",
                maintainer:"",
                curator:"",
                title:"",
                country:""
            },
        };
    },
    beforeDestroy(){
        this.$store.state.info = null;
    },
    beforeMount(){
        this.getConfig()
    },
    methods:{
        getConfig(){
            this.$axios({
            method: 'get',
            url: new URL('/api/configuration/get', this.$env.apiBaseUrl),
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('jwt')
            }
            })
            .then(response => {
                this.configuration = response.data
            })
            .catch(error => {
                if(error.response.status === 401){
                    this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                    this.$router.push("/login")                            
                }
            })
        },
        submit(){
            let geonameId = ""
            if (this.geoname == "") {
                document.getElementById("connectorCountry").required = true
            }
            for (let i in this.countries){
                if (this.countries[i].countryName == this.geoname){
                    geonameId = this.countries[i].geonameId
                }
            }
            let gemapptesLand = "https://sws.geonames.org/"+geonameId
            this.configuration.country = gemapptesLand
            this.$axios({
                method: 'post',
                url: new URL('/api/configuration/edit', this.$env.apiBaseUrl),
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                },
                data: this.configuration
                })
                .then(response => {
                    this.$store.dispatch('update', response.data)
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

<style>

</style>