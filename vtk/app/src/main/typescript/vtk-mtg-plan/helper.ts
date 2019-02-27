declare const ___ENV___;

export module HELPER {
    export module FORMAT {
        export const TIME_STD = 'MMMM DD hh:mm a';
        export function convertMinsToHrsMins(mins) {
            let h:number | string = Math.floor(mins / 60);
            let m:number | string = mins % 60;
            h = h > 12 ? h - 12: h;
            m = m < 10 ? '0' + m : m;
            return `${h}:${m}`;
          }
    }
    export module PATH {
        const URL = {
            'MTG_IMAGES' : '/content/dam/girlscouts-vtk/local/icon/meetings/'
        }

        export function URL_MAKER(type:string, file=''){
            return  URL[type] + file;
        } 
    }


     export const objectIsEmpty = (obj) => Object.keys(obj).length && obj.constructor === Object;

    export class INTERVAL {
        private holder: number | undefined;
        private timeOut: number;
        private Fn: Function;
        time: number;

        constructor(fn:Function,time:number){
            this.Fn = fn;
            this.time = time;
        }

        public on(){
            return this.holder = setInterval(this.Fn,this.time);
        }

        public off(){
            clearInterval(this.holder);
            this.holder = undefined;
        }


        public skip(timeout){
            this.off();
            const _timeout = timeout || 100;
            this.timeOut = setTimeout(() => {
                this.on();
                this.timeOut = undefined;
            }, _timeout);
        }

    }    
}