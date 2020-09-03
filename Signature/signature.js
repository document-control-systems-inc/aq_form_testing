	
	
	function viewSignature(myField) {
		let divSelected = document.querySelector('#' + myField);
		alert('Valor: ' + divSelected.value);
	}

	//======================================================================
    // VARIABLES
    //======================================================================
	let idDiv = 'signature-modal';
	let idCanvas = 'signature';
	let idField;
	
	let arrayFirmas = [];
	let myIndex = -1;
	
    let miCanvas;
	
    let lineas = [];
    let correccionX = 0;
    let correccionY = 0;
    let pintarLinea = false;
    let posicion;
	let objFirma = {};
	
	function showSignature(myfield) {
		myIndex = -1;
		lineas = [];
		
		arrayFirmas.forEach(function (firma, index) {
			if (firma.id == myfield) {
				myIndex = index;
				objFirma = firma;
				lineas = objFirma.lineas;
			}
		});	
		if (myIndex == -1) {
			objFirma = {};
			objFirma.id = myfield;
			objFirma.lineas = [];
		}
		
		idField = myfield;
		let divSelected = document.querySelector('#' + idDiv);
		divSelected.style.display='block';
		
		miCanvas = document.querySelector('#' + idCanvas);
		posicion = miCanvas.getBoundingClientRect();
		correccionX = posicion.x;
		correccionY = posicion.y;
		miCanvas.width = 384;
		miCanvas.height = 160;
		let ctx = miCanvas.getContext("2d");
		ctx.canvas.style.cursor = "crosshair";
		muestraLineas(ctx);
		
		//======================================================================
		// EVENTOS
		//======================================================================

		// Eventos raton
		miCanvas.addEventListener('mousedown', empezarDibujo, false);
		miCanvas.addEventListener('mousemove', dibujarLinea, false);
		miCanvas.addEventListener('mouseup', pararDibujar, false);

		// Eventos pantallas táctiles
		miCanvas.addEventListener('touchstart', empezarDibujo, false);
		miCanvas.addEventListener('touchmove', dibujarLinea, false);
		
		
	}
	
	
	function muestraLineas(ctx) {
		// Estilos de linea
		ctx.lineJoin = ctx.lineCap = 'round';
		ctx.lineWidth = 2;
		// Color de la linea
		ctx.strokeStyle = '#0000ff';
		// Redibuja todas las lineas guardadas
		ctx.beginPath();
		
		lineas.forEach(function (segmento) {
			ctx.moveTo(segmento[0].x, segmento[0].y);
			segmento.forEach(function (punto, index) {
				ctx.lineTo(punto.x, punto.y);
			});
		});				
		ctx.stroke();
	}
	
	function borrarSignature() {
		miCanvas = document.querySelector('#' + idCanvas);
		let ctx = miCanvas.getContext("2d");
		ctx.fillStyle = "white";
		ctx.fillRect(0, 0, 384, 160);
		lineas = [];
	}
	
	function cerrarSignature() {
		let divSelected = document.querySelector('#' + idDiv);
		divSelected.style.display='none';
		miCanvas = document.querySelector('#' + idCanvas);
		
		miCanvas.removeEventListener('mousedown', empezarDibujo, false);
		miCanvas.removeEventListener('mousemove', dibujarLinea, false);
		miCanvas.removeEventListener('mouseup', pararDibujar, false);

		// Eventos pantallas táctiles
		miCanvas.removeEventListener('touchstart', empezarDibujo, false);
		miCanvas.removeEventListener('touchmove', dibujarLinea, false);
		
	}
	
	function aceptarSignature() {
		let fieldSelected = document.querySelector('#' + idField);
		let canvasSelected = document.querySelector('#' + idCanvas);
		fieldSelected.value = canvasSelected.toDataURL('image/png');
		cerrarSignature();
		objFirma.lineas = lineas;
		if (myIndex != -1) {
			arrayFirmas.splice(myIndex, 1);
		}
		arrayFirmas.push(objFirma);
		
		
		
	}
	
    //======================================================================
    // FUNCIONES
    //======================================================================

    /**
     * Funcion que empieza a dibujar la linea
     */
    function empezarDibujo () {
        pintarLinea = true;
        lineas.push([]);
    };

    /**
     * Funcion dibuja la linea
     */
    function dibujarLinea (event) {
        event.preventDefault();
        if (pintarLinea) {
			let ctx = miCanvas.getContext('2d')
            // Marca el nuevo punto
            let nuevaPosicionX = 0;
            let nuevaPosicionY = 0;
            if (event.changedTouches == undefined) {
                // Versión ratón
                nuevaPosicionX = event.layerX;
                nuevaPosicionY = event.layerY;
            } else {
                // Versión touch, pantalla tactil
                nuevaPosicionX = event.changedTouches[0].pageX - correccionX;
                nuevaPosicionY = event.changedTouches[0].pageY - correccionY;
            }
            // Guarda la linea
            lineas[lineas.length - 1].push({
                x: nuevaPosicionX,
                y: nuevaPosicionY
            });
            muestraLineas(ctx);
        }
    }

    /**
     * Funcion que deja de dibujar la linea
     */
    function pararDibujar () {
        pintarLinea = false;
    }
