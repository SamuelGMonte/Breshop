const body = $('body');
const sectionColor = $('.sectionColor'); 
const spanTag = $('.underline-scroll-animation');

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

    const red = 224 - Math.round(140 * scrollFraction);
    const green = 224 - Math.round(140 * scrollFraction);
    const blue = 224 - Math.round(140 * scrollFraction);

    const bColor = `rgb(${red}, ${green}, ${blue})`;

    const totalHeight = $(document).height()

    if(topScroll >= totalHeight/2) {
        spanTag.addClass('active');
    } else {
        spanTag.removeClass('active');
    }
    

    body.css("backgroundColor", bColor); 
});
