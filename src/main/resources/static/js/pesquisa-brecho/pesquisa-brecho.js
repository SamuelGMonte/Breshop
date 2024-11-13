$(document).ready(function() {
    $.when(
        $.ajax({ url: "/api/v1/brecho/nomes" }),   
        $.ajax({ url: "/api/v1/brecho/enderecos" }) ,
        $.ajax({ url: "/api/v1/brecho/sites" }),
        $.ajax({ url: "/api/v1/brecho/imagens" })
    ).then(function(nomesResponse, enderecosResponse, siteResponse, imageResponse) {
        
        if (nomesResponse[0].status === "success" && enderecosResponse[0].status === "success" && siteResponse[0].status === "success" && imageResponse[0].status === "success") {
            const nomes = nomesResponse[0].nomes;
            const enderecos = enderecosResponse[0].enderecos;
            const site = siteResponse[0].websites;        
            const images = imageResponse[0].brechos.map(brecho => brecho.imagem);
        
            const resizePromises = images.map((img, index) => {
                return resizeBase64Img(img, 500, 350);  
            });
        
            Promise.all(resizePromises).then((resizedImages) => {
                // Create combinedData inside the Promise.all block
                const combinedData = nomes.map(function(nome, index) {
                    return {
                        nome: nome,
                        endereco: enderecos[index],
                        site: site[index],
                        img: resizedImages[index] || null
                    };
                });
            
            const container = $('#cardContainer');
            
            combinedData.forEach(function(data) {
                const url = data.site ? (data.site.startsWith("http://") || data.site.startsWith("https://") ? data.site : `http://${data.site}`) : '#';
                const imgTag = data.img ? 
                    `<img class="card-img-top" src="data:image/png;base64,${data.img}" alt="Imagem do brechó">` : 
                    `<img class="card-img-top">`;
    
                const card = `
                <div class="col-6 mt-4">
                    <div class="card">
                    ${imgTag}
                        <div class="card-body">
                            <h5 class="card-title">${data.nome}</h5>
                            <p class="card-text">Endereço: ${data.endereco}</p>
                            <p class="card-text">Site: <a href="${url}" target="_blank" rel="noopener noreferrer">${data.site || 'Site não informado'}</a></p>
                            <button class="btn leitura-btn">Ler mais</button>
                        </div>
                    </div>
                </div>
                `;
                container.append(card);
            });

        })
        }
        else {
            console.log("Erro ao carregar os dados.");
        }
        });
    });

function resizeBase64Img(base64, width, height) {
    return new Promise((resolve) => {
        const img = new Image();
        img.src = `data:image/png;base64,${base64}`;
        
        img.onload = function() {
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            canvas.width = width;
            canvas.height = height;
            ctx.drawImage(img, 0, 0, width, height);
            const resizedBase64 = canvas.toDataURL('image/png').split(',')[1];
            resolve(resizedBase64); // Resolve the resized image
        };
    });
}
