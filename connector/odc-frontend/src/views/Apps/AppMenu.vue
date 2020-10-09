<template>
    <div class="wrapper">
        <ul class="list-group" style="width: 1098px;height: 36px;">
            <li v-for="app in images" class="list-group-item" v-bind:key=app.uuid v-bind:class="{ disabled: app.containers===0}">
                <div class="row">
                    <div class="col-10">
                    {{app.name}}
                    </div>
                    <div class="col-1">
                        <button v-on:click="startAction(app.uuid)" class="btn btn-primary" v-bind:class="{ disabled: app.containers!=0}">Start</button>
                    </div>
                    <div class="col-1">
                        <button v-on:click="stopAction(app.uuid)" class="btn btn-primary mb1 bg-red" v-bind:class="{ disabled: app.containers===0}">Stop</button>
                    </div>
                </div>
            </li>
        </ul>   
    </div>
</template>
            
<script>
export default {
    name:"Apps",
    data() {
        return {
            images: null
        };
    },
    beforeMount(){
        this.updateParams()
    },
    watch: {
        '$route': 'updateParams'
    },
    methods:{
          updateParams(){
            this.$axios({
                method: 'get',
                url: new URL('/api/images', this.$env.apiBaseUrl),
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.images = response.data
            })
            .catch(error => {
                if(error.response.status === 401){
                    this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                    this.$router.push("/login")
                }
            })
        },
        startAction(uuid){
            this.$axios({
                method: 'post',
                url: new URL('/api/images/start', this.$env.apiBaseUrl),
                data: uuid,
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.$store.dispatch('update',response.data)
                this.updateParams()
            })
            .catch(error => {
                if(error.response.status === 401){
                    this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                    this.$router.push("/login")                            
                }
            })
        },
        stopAction(uuid){
            this.$axios({
                method: 'post',
                url: new URL('/api/images/stop', this.$env.apiBaseUrl),
                data: uuid,
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.$store.dispatch('update',response.data)
                this.updateParams()
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