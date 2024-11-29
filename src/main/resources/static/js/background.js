const body = $('body');
const sectionColor = $('.sectionColor'); 
const spanTag = $('.underline-scroll-animation');

const h1 = $('h1');
const btnProcura = $('.btn-procura');
const btnCadastro = $('.btn-cadastro');
const p = $('p');

body.css("backgroundColor", "rgb(224, 224, 224)");

sectionColor.each((index, section) => {
    const bgColors = [
        'rgb(224, 224, 224)',
        'rgb(231, 231, 231)',
        'rgb(232, 232, 232)',
        'rgb(234, 234, 234)'
    ];

    $(section).css("backgroundColor", bgColors[index % bgColors.length]);
});

$(window).on("scroll", function() {
    const topScroll = $(window).scrollTop(); 
    const maximumTopScroll = body.prop('scrollHeight') - $(window).height(); 
    const scrollFraction = topScroll / maximumTopScroll;

    const red = 224 - Math.round(250 * scrollFraction);
    const green = 224 - Math.round(250 * scrollFraction);
    const blue = 224 - Math.round(250 * scrollFraction);

    const red2 = Math.round(350 * scrollFraction);
    const green2 =  Math.round(350 * scrollFraction);
    const blue2 =  Math.round(350 * scrollFraction);


    const bColor = `rgb(${red}, ${green}, ${blue})`;
    const elColor = `rgb(${red2}, ${green2}, ${blue2})`;

    const totalHeight = $(document).height()

    if(topScroll >= totalHeight/2) {
        spanTag.addClass('active');
    } else {
        spanTag.removeClass('active');
    }
    
    h1.css("color", elColor);
    p.css("color", elColor);
    btnProcura.css("color", elColor);
    btnCadastro.css("color", elColor);
    body.css("backgroundColor", bColor); 
});
