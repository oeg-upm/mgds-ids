<template>
  <div class="row">
        <div class="col-lg-12">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Registrierte Broker</h5>
                    <div class="card-text">
                         <table class="table">
                            <thead>
                                <tr>
                                    <th scope="col">ID</th>
                                    <th scope="col">URL</th>
                                    <th scope="col">Status</th>
                                    <th scope="col">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="broker in brokers" v-bind:key="broker.id">
                                    <th scope="row">{{broker.id}}</th>
                                    <td>{{broker.url}}</td>
                                    <td>
                                        <span v-if="broker.status === 'REGISTERED'">
                                            <span class="badge badge-success">Registriert</span>
                                        </span>
                                        <span v-if="broker.status === 'UNREGISTERED'">
                                            <span class="badge badge-info">Nicht registriert</span>
                                        </span>
                                    </td>
                                    <td>
                                        <button v-on:click="toggleBroker(broker.id, broker.status)" class="btn btn-primary">
                                            <span v-if="broker.status === 'REGISTERED'">Abmelden</span>
                                            <span v-else>Registrieren</span>
                                        </button>
                                        <button v-on:click="deleteBroker(broker.id)" class="btn btn-danger">Löschen</button>
                                    </td>
                                </tr>
                            </tbody>
                         </table>
                    </div>
                    <div class="card-text">
                        <div class="form-group">
                                <label for="url">Broker URL</label>
                                <input type="text"
                                    class="form-control"
                                    id="url"
                                    name="url"
                                    aria-describedby="resourceIDHelp"
                                    placeholder="http://localhost:8080/broker"
                                    v-model="url">
                        </div>
                        <button v-on:click="submit()" class="btn btn-primary">Neuen Broker registrieren</button>
                    </div>
                </div>
            </div>
        </div>
  </div>
</template>

<script>
export default {
    name:"Brokers",
    data() {
        return {
            brokers:[],
            url:null
        };
    },
    beforeDestroy(){
        this.$store.state.info = null;
    },
    beforeMount(){
        this.findBroker()
    },
    methods:{
        submit(){
                this.$axios({
                    method: 'post',
                    url: new URL('/api/broker/add', this.$env.apiBaseUrl),
                    data: {
                        url: this.url
                    },
                    headers: {
                         Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$store.dispatch('update',response.data)
                    this.findBroker()
                })
                .catch(error => {
                    if(error.response.status === 401){
                        this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                        this.$router.push("/login")                            
                    }
                })  
        },
        toggleBroker(id, status){
            var url;
            if(status === 'REGISTERED'){
                url = new URL('/api/broker/unregister/'+id, this.$env.apiBaseUrl)
            }
            else{
                url = new URL('/api/broker/register/'+id, this.$env.apiBaseUrl)
            }

             this.$axios({
                    method: 'get',
                    url: url,
                    headers: {
                         Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$store.dispatch('update',response.data)
                    this.findBroker()
                })
                .catch(error => {
                    if(error.response.status === 401){
                        this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                        this.$router.push("/login")                            
                    }
                })  

        },
        findBroker(){
            this.$axios({
                    method: 'get',
                    url: new URL('/api/broker/findAll', this.$env.apiBaseUrl),
                    headers: {
                         Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.brokers = response.data
                })
                .catch(error => {
                    if(error.response.status === 401){
                        this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                        this.$router.push("/login")                            
                    }
                })  
        },
        deleteBroker(id){
            this.$axios({
                    method: 'get',
                    url: new URL('/api/broker/delete/'+id, this.$env.apiBaseUrl),
                    headers: {
                         Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$store.dispatch('update',response.data)
                    this.findBroker()
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