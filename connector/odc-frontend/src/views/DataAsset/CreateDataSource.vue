<template>
    <div class="wrapper" style="display: flex;justify-content: space-between;">
        <sidebar v-bind:sources=this.sources v-on:delete="deleteAction"></sidebar>
        <div id="content" >
            <div class="card" style="margin-bottom: 20px;">
                <div class="card-body">
                    <p v-if="typeof this.sourceid !== 'undefined'" class="card-text">
                        Datenquelle <strong class="nummer-name-dataset">Nr.{{this.id}}</strong>
                        <strong class="nummer-name-dataset">{{this.name}}</strong>
                        des Adapters <strong class="nummer-name-dataset">{{this.typeAdapter}}</strong> bearbeiten . <br>
                        Bitte füllen Sie alle Felder aus.
                    </p>
                    <p v-else class="card-text">
                        Datenquelle für den Adapter <strong class="nummer-name-dataset">{{this.typeAdapter}}</strong> registrieren . <br>
                        Bitte füllen Sie alle Felder aus.
                    </p>
                </div>
            </div>
            <div class="row" style="margin-bottom: -10px">
                <div class="col">
                    <div class="form-group">
                        <label for="datasourceName">Data Source Name</label>
                        <input type="text"
                        class="form-control"
                        id="datasourceName"
                        name="datasourceName"
                        :required =true
                        placeholder="My Data Source"
                        v-model="name">
                    </div>
                </div>
                <div class="col">
                    <div class="form-group">
                        <label for="datasourceType">Data Source Type</label>
                        <input type="text"
                               class="form-control"
                               id="datasourceType"
                               name="datasourceType"
                               placeholder="My Data Source"
                               :disabled=true
                               style="color: #9d9a9a"
                               :required =true
                               v-model="type">
                        <!--select class="form-control"
                        id="datasourceType"
                        name="datasourceType"
                        v-model="type"
                        :disabled="true">
                        <option v-for="option in sources" 
                        v-bind:key=option.type
                        v-bind:value="option.type"
                        :selected="option.type === type"
                        >{{ option.type }}</option>
                        </select-->
                    </div>
                </div>
            </div>
            <div class="row">
                <ncform :form-schema="formSchema" form-name="dataInput" v-model="formSchema.value"></ncform>
            </div>
            <div class="row" style="margin-left: auto ; margin-top: 20px;">
                <button v-on:click="submit()" class="btn btn-primary">Data Source hinzufügen</button>
            </div>
        </div>
    </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'

export default {
  name: 'createdatasource',
  components: {
    Sidebar
  },
  props:['sourceid','typeAdapter'],
    data() {
        return {
            type: "",
            name: null,
            id: null,
            formSchema: {
                type:"object",
                properties:{}
            },
            sources: []
        };
    },
    beforeMount(){
        this.querySources()
    },
    watch: {
        '$route': 'getFormSchema',
        type: function() {
            this.getFormSchema()
        },
        sources: function() {
            this.type = this.sources[0].type
            this.getFormSchema()
        }
    },
    methods:{
        getFormSchema(){
            console.log("Type "+this.sourceid)
            this.type = this.typeAdapter
              this.$axios({
                    method: 'get',
                    url: new URL('/api/datasources/schema/type/'+this.typeAdapter, this.$env.apiBaseUrl),
                    headers: {
                         Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.formSchema = response.data
                    this.updateParams()
                })
                .catch(error => {
                    if(error.response.status === 401){
                        this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                        this.$router.push("/login")
                    }
                })
        },
        updateParams(){
            if(typeof this.sourceid !== 'undefined'){
                this.id = this.sourceid
                this.$axios({
                    method: 'get',
                    url: new URL('/api/datasources/find/id/'+this.id, this.$env.apiBaseUrl),
                    headers: {
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                    .then(response => {
                        this.type=response.data.source.datasourcetype
                        this.name = response.data.source.datasourcename
                        this.formSchema.value = JSON.parse(response.data.source.data)
                    })
                    .catch(error => {
                        if(error.response.status === 401){
                            this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                            this.$router.push("/login")
                        }
                    })
            }
            else{
                this.name = null
                this.id = null
            }
        },
        submit(){
            let url;
            if(this.id === null){
                url = new URL('/api/datasources/add', this.$env.apiBaseUrl)
            }
            else{
                url = new URL('/api/datasources/edit/'+this.id, this.$env.apiBaseUrl)
            }
            this.$axios({
                method: 'post',
                url: url,
                data: {
                    datasourcetype: this.type,
                    datasourcename: this.name,
                    data: this.formSchema.value
                },
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.$store.dispatch('update',response.data)
                this.$router.push("/datasource/select")  
            })
            .catch(error => {
                if(error.response.status === 401){
                    this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                    this.$router.push("/login")                            
                }
            }) 
        },
        deleteAction(id){
         this.$axios({
                method: 'get',
                url: new URL('/api/datasources/delete/'+id, this.$env.apiBaseUrl),
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.$router.push("/datasource/select")
                this.$store.dispatch('update',response.data)
            })
            .catch(error => {
                if(error.response.status === 401){
                    this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                    this.$router.push("/login")                            
                }
            })
        },
        querySources(){
                this.$axios({
                    method: 'get',
                    url: new URL('/api/listAdapters', this.$env.apiBaseUrl),
                    headers: {
                         Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.sources = []
                    for( var i in response.data.sort() ){
                        var adapter = response.data[i]
                         this.$axios({
                            method: 'get',
                            url: new URL('/api/datasources/find/type/'+adapter.name, this.$env.apiBaseUrl),
                            headers: {
                                Authorization: 'Bearer ' + localStorage.getItem('jwt')
                            }
                        })
                        .then(response2 => {
                            this.sources.push({
                                type: response2.data.type,
                                sources: response2.data.result
                            })
                        })
                        .catch(error2 => {
                            if(error2.response.status === 401){
                                this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                                this.$router.push("/login")                            
                            }
                        })    
                    }
                   })
                .catch(error => {
                    if(error.response.status === 401){
                        this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                        this.$router.push("/login")                            
                    }
                })
        },
    }
}
</script>
<style>
    #content{
        width: 100%;
        margin-left: 50px;
    }
    .__object-form-item{
        margin-top: 0px;
    }
    .form-control:disabled {
        background-color:#303030;
    }
    .nummer-name-dataset{
        color: #3498DB;
    }
    .ncform {
        width: 1000px;
    }

</style>