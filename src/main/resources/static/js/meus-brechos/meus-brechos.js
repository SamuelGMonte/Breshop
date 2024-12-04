var currentId;
$(document).ready(function () {
    $.when(
        $.ajax({ url: `/api/v1/brecho/meus-brechos` }),
        $.ajax({ url: `/api/v1/brecho/endereco` }),
        $.ajax({ url: `/api/v1/brecho/site` }),
        $.ajax({ url: `/api/v1/brecho/imagem` }),
        $.ajax({ url: `/api/v1/brecho/brechos`})
    ).then(function (nomesResponse, enderecosResponse, siteResponse, imageResponse, brechosId) {
        if (
            nomesResponse[0].status === "success" &&
            enderecosResponse[0].status === "success" &&
            siteResponse[0].status === "success" &&
            imageResponse[0].status === "success"
        ) {
            const nomes = nomesResponse[0].nomes[0];
            const endereco = enderecosResponse[0].enderecos[0];
            const site = siteResponse[0].websites[0];
            const image = imageResponse[0].imagem[0];

            const combinedData = [
                {
                    nome: nomes,
                    endereco: endereco,
                    site: site,
                    img: image || null,
                },
            ];

            const container = $(".container-login");

            combinedData.forEach(function (data) {
                
                brechosId[0].find(brecho => {
                    if(data.nome === brecho.nome) {
                        currentId = brecho.id;
                        return;
                    };
                })

                const url = data.site
                    ? data.site.startsWith("http://") || data.site.startsWith("https://")
                        ? data.site
                        : `http://${data.site}`
                    : "#";
                const imgTag = `<img class="card-img-top" src="data:image/png;base64,${data.img}" onerror="this.onerror=null;this.src='../assets/store.png';" >`;

                const card = `
                <div class="col-12 mt-4">
                    <div class="card">
                    <div class="card-body">
                        <div class="flex-container">
                            <div class="left-content">
                                <h5 class="card-title">${data.nome}</h5>
                                <p class="card-text">Endereço: ${data.endereco}</p>
                                <p class="card-text">Site: <a href="${url}" target="_blank" rel="noopener noreferrer">${data.site || "Site não informado"}</a></p>
                            </div>
                            <div class="right-img">
                                ${imgTag}
                            </div>
                        </div>
                        
                            <div id="accordion">
                            <div class="card">
                                <div class="card-header" id="headingOne">
                                <h5 class="mb-0">
                                    <button class="btn btn-link" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                    Descrição &darr;
                                    </button>
                                </h5>
                                </div>
                
                                <div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordion">
                                <div class="card-body">
                                    <textarea id="editableText" maxlength="500" rows="3" placeholder="Coloque a descrição do seu brechó aqui" class="form-control"></textarea>
                                    <button id="saveButton" class="btn btn-primary mt-2">Salvar</button>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                `;

                container.append(card);
                
            });


            $('#editableText').each(function () {
                const $textarea = $(this);

                $.ajax({
                    url: `/api/v1/brecho/descricao/${currentId}`,
                    method: 'GET',
                    success: function (response) {
                        $textarea.val(response.message);
                    },
                    error: function (error) {
                        console.error('Falha ao carregar descrição:', error);
                    }
                });
            });
        } else {
            console.log("Erro ao carregar os dados.");
        }
    });

})


$(document).on('click', '#saveButton', function() {
    const editedText = $('#editableText').val(); 
    
    if(editedText === null || editedText === "") {
        Swal.fire({
            icon: 'error',
            title: 'Descrição vazia',
            text: 'Não foi possivel salvar a descrição vazia!',
            confirmButtonText: 'OK'
        });
        return;
    }

    $.ajax({
        url: '/api/v1/brecho/salvar-descricao', 
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ descricao: editedText }),
        success: function(response) {
            Swal.fire({
                icon: 'success',
                title: 'Texto salvo',
                text: 'Nova descrição salva com sucesso!',
                confirmButtonText: 'OK'
            });
        },
        error: function(error) {
            console.log(error);
            alert('Erro ao salvar a descrição!');
        }
    });
});

function resizeBase64Img(base64, width, height) {
    return new Promise((resolve) => {
        const img = new Image();
        img.src = `data:image/png;base64,${base64}`;

        img.onload = function () {
            const canvas = document.createElement("canvas");
            const ctx = canvas.getContext("2d");
            canvas.width = width;
            canvas.height = height;
            ctx.drawImage(img, 0, 0, width, height);
            const resizedBase64 = canvas.toDataURL("image/png").split(",")[1];
            resolve(resizedBase64); 
        };
    });
}
