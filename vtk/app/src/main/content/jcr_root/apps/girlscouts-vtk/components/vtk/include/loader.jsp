<!--Based off and taken from juno/src/components/share/vtk-loading.tsx from the juno project-->

<div id="vtk-loading" className='hide' style={{ "height":this.height }}>
    <section className="artboard">
        <div className="wrap__loader" >
            <div className="loader__circle loader__circle--1"></div>
            <div className="loader__circle loader__circle--2"></div>
            <div className="loader__circle loader__circle--3"></div>
            <div className="loader__circle loader__circle--4"></div>
        </div>
    </section>
</div>
<style>
    #vtk-loading{
        width: 100%;
        position: fixed;
        height: 100%;
        top:0;
        left:0;
        background-color: rgba(0,0,0,0.1);
        z-index: 9000;
    }



    .artboard{
        -webkit-box-flex: 2;
        -webkit-flex-grow: 2;
        -ms-flex-positive: 2;
        flex-grow: 2;
        display: -webkit-box;
        display: -webkit-flex;
        display: -ms-flexbox;
        display: flex;
        -webkit-flex-wrap: wrap;
        -ms-flex-wrap: wrap;
        flex-wrap: wrap;
        -webkit-box-align: center;
        -webkit-align-items: center;
        -ms-flex-align: center;
        align-items: center;
        -webkit-box-pack: center;
        -webkit-justify-content: center;
        -ms-flex-pack: center;
        justify-content: center;
        width: 100%;
        height: 100%;
    }

    $color-circle1:#1abc9c;
    $color-circle2:#16a085;
    $color-circle3:#f1c40f;
    $color-circle4:#f39c12;

    $circle-size:20px;

    .loader__circle{
        width: $circle-size;
        min-height: $circle-size;
        float: left;
        border-radius:100%;
        margin: 0 $circle-size/2;
        animation-name: loader;
        animation-duration: 1.1s;
        animation-iteration-count: infinite;
    }


    .loader__circle--1 {
        background-color: $color-circle1;}
    .loader__circle--2 {
        background-color: $color-circle2;
        animation-delay: .275s;}
    .loader__circle--3 {
        background-color: $color-circle3;
        animation-delay: .550s;}
    .loader__circle--4 {
        background-color: $color-circle4;
        animation-delay: .825s;}



    /* The animation code */
    @keyframes loader {
        0% {transform: translateY(0)}
        50% {transform: translateY(-70%)}
        100% {transform: translateY(0)}
    }
</style>