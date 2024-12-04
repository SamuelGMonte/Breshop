$(document).ready(function () {

    $.when(
        $.ajax({ url: `/api/v1/brecho/nomes` }),
        $.ajax({ url: `/api/v1/brecho/enderecos` }),
        $.ajax({ url: `/api/v1/brecho/sites` }),
        $.ajax({ url: `/api/v1/brecho/imagens` }),
        $.ajax({ url: `/api/v1/brecho/brechos`})
    ).then(function(nomesResponse, enderecosResponse, siteResponse, imageResponse, brechosId) {

        if (nomesResponse[0].status === "success" && 
            enderecosResponse[0].status === "success" && 
            siteResponse[0].status === "success"  ) {
            
            const nomes = nomesResponse[0].nomes;
            const enderecos = enderecosResponse[0].enderecos;
            const sites = Array.isArray(siteResponse[0].websites) ? siteResponse[0].websites : [];
            const verifiedBrechos = imageResponse[0].brechos; 

            const combinedData = nomes.map((nome, index) => {
                const normalizedNome = nome.toLowerCase().trim();
                
                const storeImage = verifiedBrechos.find(brecho => 
                    brecho.brechoNome.toLowerCase().trim() === normalizedNome
                )?.imagem || null;
            
            
                return {
                    nome,
                    endereco: enderecos[index] || 'Address not available',
                    site: sites[index] || 'Site not available',
                    img: storeImage 
                };
            });
                                              

            $("#searchForm").on("submit", function (e) {
                e.preventDefault();
                const keyword = $("#searchInput").val().toLowerCase().trim();
        
                const filteredData = combinedData.filter(item =>
                    item.nome.toLowerCase().includes(keyword) ||
                    item.endereco.toLowerCase().includes(keyword) ||
                    item.site.toLowerCase().includes(keyword)
                );
        
                const container = document.getElementById("cardContainer");
                if (container) {
                    container.innerHTML = ""; 
                    initVirtualScroller(container, filteredData);
                }
            });

            function initVirtualScroller(container, items) {
                const ITEM_HEIGHT = 250;
                const VISIBLE_ITEMS = 5;
                const BUFFER = 2;

                container.style.height = `${VISIBLE_ITEMS * ITEM_HEIGHT}px`;
                container.style.overflowY = 'scroll';
                container.style.position = 'relative';

                const wrapper = document.createElement('div');
                wrapper.style.height = `${items.length * ITEM_HEIGHT}px`;
                wrapper.style.position = 'relative';
                container.appendChild(wrapper);

                const itemCache = new Map();
                
                function createItemElement(item, index) {
                    if (itemCache.has(index)) {
                        return itemCache.get(index);
                    }

                    const getIdByName = (brechoName) => {
                        const store = brechosId[0].find(brecho => {
                            return brecho.nome === brechoName;
                        });
                        return store ? store.id : null;
                    };
                    

                    const itemElement = document.createElement('div');
                    itemElement.style.position = 'absolute';
                    itemElement.style.top = `${index * ITEM_HEIGHT}px`;
                    itemElement.style.width = '100%';
                    itemElement.style.willChange = 'transform'; 

                    const url = item.site.startsWith("http") ? item.site : "#";
                    
                    const imgTag = item.img ? `<img class="card-img-top" src="data:image/png;base64, ${item.img}" style="width: 50px" loading="lazy">` : `<img class="card-img-top" src="../assets/store.png" style="width: 50px" loading="lazy">`;
                    
                    
                    window.get_descricao = function (brechoNome) {
                        var brechoId = getIdByName(brechoNome);
                        $.ajax({
                            url: `/api/v1/brecho/descricao/${brechoId}`,
                            method: "GET",
                            success: function (descricaoResponse) {
                                if (descricaoResponse.status === "success") {
                                    $("#modalDescricao .modal-title").text("Descrição do Brechó");
                                    $("#modalDescricao .modal-body").text(`
                                        ${descricaoResponse.message}
                                    `);
                    
                                    $("#modalDescricao").modal("show");
                                } else {
                                    console.error("Failed to fetch description:", descricaoResponse);
                                }
                            },
                            error: function (error) {
                                console.error("Error during API call:", error);
                            },
                        });
                    };


                    itemElement.innerHTML = `
                        <div class="card">
                            <div class="card_content-container">
                                <div class="left-container">
                                    ${imgTag}
                                </div>
                                <div class="right-container">
                                    <div class="card-body">
                                        <h5 class="card-title">${item.nome}</h5>
                                        <p class="card-text">Endereço: ${item.endereco}</p>
                                        <p class="card-text">Site: <a href="${url}" target="_blank">${item.site}</a></p>
                                        <button class="btn leitura-btn" onclick="get_descricao('${item.nome}')" data-target="#modalDescricao">Ler mais</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;

                    itemCache.set(index, itemElement);
                    return itemElement;
                }

                function renderItems() {
                    const scrollTop = container.scrollTop;
                    const startIndex = Math.max(0, Math.floor(scrollTop / ITEM_HEIGHT) - BUFFER);
                    const endIndex = Math.min(
                        items.length - 1, 
                        Math.ceil((scrollTop + container.clientHeight) / ITEM_HEIGHT) + BUFFER
                    );

                    Array.from(wrapper.children).forEach(child => {
                        const index = parseInt(child.dataset.index);
                        if (index < startIndex || index > endIndex) {
                            wrapper.removeChild(child);
                        }
                    });

                    for (let i = startIndex; i <= endIndex; i++) {
                        if (!wrapper.querySelector(`[data-index="${i}"]`)) {
                            const itemElement = createItemElement(items[i], i);
                            itemElement.dataset.index = i;
                            wrapper.appendChild(itemElement);
                        }
                    }
                }

                let rafId = null;
                container.addEventListener('scroll', () => {
                    if (rafId) cancelAnimationFrame(rafId);
                    rafId = requestAnimationFrame(renderItems);
                });

                renderItems();
            }

            const container = document.getElementById('cardContainer');
            if (container) {
                initVirtualScroller(container, combinedData);
            } else {
                console.error('Container not found');
            }
        }
        else {
            console.log("Erro ao carregar os dados.");
        }
    });
})

