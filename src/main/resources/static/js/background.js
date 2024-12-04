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

    const red = Math.max(0, 224 - Math.round(150 * scrollFraction));
    const green = Math.max(0, 224 - Math.round(150 * scrollFraction));
    const blue = Math.max(0, 224 - Math.round(150 * scrollFraction));

    const red2 = Math.min(Math.round(250 * scrollFraction));
    const green2 = Math.min(Math.round(250 * scrollFraction));
    const blue2 = Math.min(Math.round(250 * scrollFraction));

    let bColor = `rgb(${red}, ${green}, ${blue})`;
    let elColor = `rgb(${red2}, ${green2}, ${blue2})`;

    const totalHeight = $(document).height();

    if (topScroll >= totalHeight / 3) {
        spanTag.addClass('active');
    } else {
        spanTag.removeClass('active');
    }

    h1.css("color", elColor);
    p.css("color", elColor);
    btnProcura.css("color", elColor);
    btnCadastro.css("color", elColor);
    body.css("backgroundColor", bColor); 

    if (
        h1.css("color") === body.css("background-color") &&
        p.css("color") === body.css("background-color") &&
        btnProcura.css("color") === body.css("background-color") &&
        btnCadastro.css("color") === body.css("background-color")
    ) {
        elColor = `rgb(${Math.max(0, red2 - 50)}, ${Math.max(0, green2 - 50)}, ${Math.max(0, blue2 - 50)})`;
        h1.css("color", elColor);
        p.css("color", elColor);
        btnProcura.css("color", elColor);
        btnCadastro.css("color", elColor);
    }
});

