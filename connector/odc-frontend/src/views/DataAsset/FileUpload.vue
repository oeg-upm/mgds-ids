<template>
    <div class="wrapper" style="display: flex;justify-content: space-between;">
        <sidebar v-bind:sources=this.sources v-on:delete="deleteAction"></sidebar>
        <div id="content">
            <div class="card" style="margin-bottom: 20px;background-color: #303030;">
                <div class="card-body">
                    <p class="card-text">
                        Laden Sie Daten direkt von ihrem Dateisystem hoch, um sie mittels des Konnektors verfügbar zu machen.
                    </p>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <form action="" class="file-upload-form" @submit.prevent="uploadFiles" enctype="multipart/form-data">
                        <div class="form-group">
                            <label for="datasettitle" class="licensesLabel" style="line-height: normal">Title</label>
                            <input  type="text"
                                    class="form-control"
                                    id="datasettitle"
                                    name="datasettitle"
                                    placeholder="Title"
                                    v-model="title" required >

                        </div>
                        <div class="file-upload">
                            <input id="fileInput" type="file" multiple @change.prevent="listUploads" ref="file" accept="*"  required >
                            <small :class="{ invalid: !valid }">
                                Total : {{ bytesToSize(sizes) }}
                            </small>
                        </div>
                        <ul v-show="showUploads" class="card-list">
                            <li v-for="(file, index) in attachments" v-key:id="index" class="card" v-bind:ref="`card-${index}`" v-bind:id="`card-${index}`">
                                <div class="checkbox-wrap">
                                    <div class="checkbox-wrap-inner">
                                        <input disabled type="checkbox" v-bind:id="`checkbox-${index}`" checked>
                                        <label v-bind:for="`checkbox-${index}`"></label>
                                        <span class="checkbox-sub-label">Uploaded</span>
                                    </div>
                                </div>
                                <div class="details">
                                    <h2 style="color: black">File name: {{ file.name }}</h2>
                                    <h3 style="color: black">File size: {{ bytesToSize(file.size) }}</h3>
                                </div>
                                <div class="remove-card">
                                    <button type="button" class="remove-card-btn" @click.prevent="deleteItem" v-bind:value="index"> Remove</button></div>
                            </li>
                        </ul>
                        <div class="form-group">
                            <label for="datasetnotes"class="licensesLabel" style="line-height: normal">Description</label>
                            <input type="text"
                                   class="form-control"
                                   id="datasetnotes"
                                   name="datasetnotes"
                                   placeholder="File Upload Description"
                                   v-model="description" required >

                        </div>
                        <div >
                            <label class="licensesLabel">
                                Lizenz ändern (Default: CC0 1.0)
                                <input type="checkbox" name="activeLizenz" value="Lizenz" v-on:click="showMe('divLicenses')" style="margin-left: 10px; display:block;">
                            </label>
                            <div style=" display:none ;clear: both;" id="divLicenses">
                                <select id="licenses" class="licenseSelect" name="licenses" v-model="licenseTitle">
                                    <option  class="form-control" v-for="license in licenses"
                                    >{{ license.title }}</option>
                                </select>
                                (Unter Umständen wird diese Auswahl im Datenquellenadapter geändert, z.B. wenn Lizenzinformationen aus externen Quellen vorliegen.)
                            </div>
                        </div>
                        <input class = "btn btn-primary" type="submit" style="margin-top:20px" value="Data Asset hinzufügen">
                    </form>
                </div>

            </div>
        </div>
    </div>
</template>

<script>
    import Sidebar from '@/components/Sidebar.vue'
    import licenses from '@/licenses.json'
    export default {
        name: "FileUpload",
        components: {
            Sidebar
        },
        data() {
            return {
                licenses:licenses,
                licenseTitle:"CC0 1.0",
                attachments:[],
                files: [],
                sizes:0,
                showUploads: false,
                title:null,
                description:null,
                checkboxes: [],
                type:null,
                sources: []
            };
        },
        beforeMount(){
            this.querySources()
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
            listUploads(e) {
                const fileInput = this.$refs['file'];
                this.showUploads = true;
                let files = e.target.files;
                for (let index = 0; index < files.length; index++) {
                    this.attachments.push(files[index])
                    const reader = new FileReader();
                    this.sizes = this.sizes + files[0].size
                    reader.readAsDataURL(files[index]);
                }

                setTimeout(function() {
                  fileInput.value = '';
                }, 1000);
                if (e.target.files != null) {
                    fileInput.required = false;
                }
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
            deleteItem: function(e) {
                let target = e.target.value;
                const fileInput = this.$refs['file'];
                this.sizes = this.sizes - this.attachments[target].size
                this.attachments.splice(target,1)
                if (this.attachments.length === 0){
                    this.sizes = 0;
                    fileInput.required = true;
                }
                this.files.splice(target,1)
            },
            bytesToSize(bytes) {
                const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
                if (bytes == 0) return '0 Byte';
                const i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
                return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
            },
            uploadFiles() {
                var licenseurl = ""
                var licensetitle = ""

                for (let i in this.licenses){
                    if (this.licenseTitle == this.licenses[i].title){
                        licenseurl = this.licenses[i].url
                        licensetitle = this.licenses[i].title
                    }
                }
                let formData = new FormData();
                let fileCount = this.attachments.length;
                let rawData = {
                    datasettitle: this.title,
                    datasetnotes: this.description,
                    licenseurl: licenseurl,
                    licensetitle: licensetitle,
                }
                rawData = JSON.stringify(rawData)
                for(let i = 0; i < fileCount; i++) {
                    formData.append(this.attachments[i].name, this.attachments[i]);
                }
                formData.append("data",rawData)
                this.$axios({
                    method: 'post',
                    url: new URL('/api/upload/file', this.$env.apiBaseUrl),
                    data: formData,
                    headers: {
                        'Content-Type': 'multipart/form-data ; application/json',
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                }).then( response => {
                    this.$store.dispatch('update',response.data)
                    this.$router.push("/job")
                }).catch(error => {
                    if(error.response.status === 401){
                         this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                         this.$router.push("/login")
                    }
                    console.log(error.response.status)
                 });

            },
        }

    }
</script>

<style scoped>
    #content{
        width: 100%;
        margin-left: 50px;
    }
    :root {

    --black: #393838;
        --red: #f2214b;
        --green: #41f290;
        --pink: #fcf4f6;

    --easing-cubic-bezier: cubic-bezier(0.04, 2.37, 0.25, 1);
        --box-shadow: 0 0 12px rgba(0,0,0,0.4);
    }
    .card-list {
        list-style: none;
        margin: 0;
        padding: 0;
    }
    .file-upload {
        box-shadow: var(--box-shadow);
        margin-bottom: 1rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: inherit;
    }

    .card {
        background: white;
        box-shadow: var(--box-shadow);
        margin-bottom: 1rem;
        display: -webkit-box;
        -webkit-box-orient:horizontal;
        align-items: center;
        justify-content: space-around;
        transition: all 200ms ease;
        position: relative;
    }
    [type="checkbox"] {
        display: none;
    }
    [type="checkbox"] ~ label {
        display: block;
        border: 2px solid black;
        padding: 1rem;
        border-radius: 2rem;
        margin: 0 20px;
        opacity: .4;
        position: relative;
    }
    [type="checkbox"] ~ label:before {
        content: '\2714';
        font-size: 1.2rem;
        font-family: "Font Awesome 5 Free";
        display: block;
        color: black;
        position: absolute;
        transform: scale(0.8);
        transform-origin: 50% 50%;
        opacity: 0;
        transition: scale 300ms var(--easing-cubic-bezier), opacity 300ms var(--easing-cubic-bezier);
        left: .4rem;
        top: .4rem;
    }
    [type="checkbox"]:checked ~ label:before {
        transform: scale(1.3);
        transition: scale 300ms var(--easing-cubic-bezier), opacity 300ms var(--easing-cubic-bezier);
        opacity: 1;
    }

    .checkbox-sub-label {
        display: block;
        color: black;
        opacity: .4;
        font-size: 11px;
        text-align: center;
        margin: .6rem;
    }
    .checkbox-wrap {
        background: var(--green);
        align-self: stretch;
        display: flex;
        padding-top: 1rem;
        flex-direction: column;
        align-items: center;
        justify-content: space-around;
        flex-wrap: wrap;
    }

    .details {
        padding: 0 .6rem;
        flex-grow: 2;
    }
    #content{
        width: 100%;
        margin-left: 50px;
    }

</style>