<template>
    <div class="wrapper"  style="display: flex;justify-content: space-between;">
        <sidebar v-bind:sources=this.sources v-on:delete="deleteAction"></sidebar> 
        <div id="content">
                <div class="card">
                    <div class="card-body">
                        <p class="card-text">
                            Data Asset der Datenquelle <strong class="nummer-name-dataset">Nr.{{this.id}}</strong>
                            <strong class="nummer-name-dataset">{{this.name}}</strong> registrieren. <br>
                            Bitte füllen Sie alle Felder aus.
                        </p>
                    </div>
                </div>
            <ncform :form-schema="formSchema" form-name="dataInput" v-model="formSchema.value" style="margin-left: -13px;"></ncform>
            <div >
                <label class="licensesLabel">
                    Lizenz ändern (Default: CC0 1.0)
                    <input type="checkbox" name="activeLizenz" value="Lizenz" v-on:click="showMe('divLicenses')" style="margin-left: 10px;">
                </label>
                <div style=" display:none ;clear: both;" id="divLicenses">
                    <select id="licenses" class="licenseSelect" name="licenses" v-model="licenseTitle">
                        <option  class="form-control" v-for="license in licenses"
                        >{{ license.title }}</option>
                    </select>
                    (Unter Umständen wird diese Auswahl im Datenquellenadapter geändert, z.B. wenn Lizenzinformationen aus externen Quellen vorliegen.)
                </div>
            </div>
            <button v-on:click="submit()" class="btn btn-primary" style="margin-top: 20px">Data Asset hinzufügen</button>
        </div>
    </div>
</template>

            
<script>
import Sidebar from '@/components/Sidebar.vue'
import licenses from '@/licenses.json'

export default {
    name:"Create",
    components: {
        Sidebar
    },
    props:['sourceid'],
    data() {
        return {
            licenses:licenses,
            licenseTitle:"CC0 1.0",
            formSchema: {
                type:"object",
                properties:{}
            },
            name: null,
            id: null,
            source:null,
            type: "",
            sources:[]
        };
    },
    beforeMount(){
        this.querySources(),
        this.updateParams()
    },
    watch:{
        '$route': 'updateParams',
        id: function() {
            this.updateParams()
        },
        sources: function() {
            this.updateParams()
        }
    },
    methods:{
        showMe (box) {
        var chboxs = document.getElementsByName("activeLizenz");
        var vis = "none";
        for(var i=0;i<chboxs.length;i++) {
            if(chboxs[i].checked){
                vis = "block";
                break;
            }
        }
        document.getElementById(box).style.display = vis;
        },
        handleFileUpload(){
            this.file = this.$refs.file.files[0];
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
                        this.formSchema = response.data.formSchema
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
        deleteAction(id){
         this.$axios({
                method: 'get',
                url: new URL('/api/datasources/delete/'+id, this.$env.apiBaseUrl),
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.querySources()
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
                    for( var i in response.data.sort()){
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
        submit(){
            var licenseurl = ""
            var licensetitle = ""

            for (let i in this.licenses){
               if (this.licenseTitle == this.licenses[i].title){
                   licenseurl = this.licenses[i].url
                   licensetitle = this.licenses[i].title
               }
            }
            this.$axios({
                method: 'post',
                url: new URL('/api/dataassets/add', this.$env.apiBaseUrl),
                data: {
                    sourceId: this.sourceid,
                    data: this.formSchema.value,
                    datasourcetype: this.type,
                    licenseurl:licenseurl,
                    licensetitle:licensetitle
                },
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.$store.dispatch('update',response.data)
                this.$router.push("/job")
            })
            .catch(error => {
                if(error.response.status === 401){
                    this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                    this.$router.push("/login")                            
                }
            })
        },
        uploadFile(){
            let formData = new FormData()
            formData.append('file', this.file)
            this.$axios({
                method: 'post',
                url: new URL('/api/dataassets/add', this.$env.apiBaseUrl),
                data: formData,
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt'),
                    'Content-Type': 'multipart/form-data'
                }
            })
            .then(response => {
                this.$store.state.info = response.data;
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
                    for( var i in response.data ){
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
        }
    }
}
</script>

<style lang="scss" scoped>
    @import '../../styles/bootstrap_theme';
    .nummer-name-dataset{
        color: #3498DB;
    }
    #content{
        width: 970px;
        margin-left: 50px;
    }
    .__object-form-item{
        margin-top: 0px;
    }
    .ncform{
        width: 1000px;
    }
</style>
