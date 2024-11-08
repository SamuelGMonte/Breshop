$(document).ready(function() {
    $.when(
        $.ajax({ url: "/api/v1/brecho/nomes" }),   
        $.ajax({ url: "/api/v1/brecho/enderecos" }) ,
        $.ajax({ url: "/api/v1/brecho/sites" }) 
    ).then(function(nomesResponse, enderecosResponse, siteResponse) {
        
        if (nomesResponse[0].status === "success" && enderecosResponse[0].status === "success" && siteResponse[0].status == "success") {
            const nomes = nomesResponse[0].nomes;
            const enderecos = enderecosResponse[0].enderecos;
            const site = siteResponse[0].websites;

            const combinedData = nomes.map(function(nome, index) {
                return {
                    nome: nome,
                    endereco: enderecos[index],
                    site: site[index]  
                };
            });

            $('#brecho_tbl').DataTable({
                data: combinedData, 
                columns: [
                    { "data": "nome" },        
                    { "data": "endereco" },     
                    { "data": "site" }     
                ],
                language: {
                    url: 'https://cdn.datatables.net/plug-ins/2.1.8/i18n/pt-BR.json',
                },
            });
        } else {
            console.log("Erro ao carregar os dados.");
        }
    });
});