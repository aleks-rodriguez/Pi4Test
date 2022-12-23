const init = () => {
  //<head> Cambiamos el titulo
  document.getElementsByTagName('title')[0].innerText = 'Unit Testing Report';

  //<body>
  //Eliminamos los divs 'banner', 'breadcrumbs' y 'leftcolumn' puesto que no 
  //aportan nada
  document.getElementById('banner').remove();
  document.getElementById('breadcrumbs').remove();
  document.getElementById('leftColumn').remove();
  document.getElementsByClassName('clear')[0].remove();
  document.getElementById('footer').remove();

  const divContent = document.getElementById('contentBox');
  //Primera 'section' dentro del div 'contentBox'
  //Modificamos el titulo y eliminamos el <script/>
  divContent.children[0].remove();
  divContent.children[0].children[0].innerText = '#Project Name'
  divContent.children[0].appendChild(document.createElement('br'));

  divContent.children[1].children[6].appendChild(document.createElement('p'))
  divContent.children[1].children[6].children[0].innerText = 'Note: The test failures details, if any, can be found on they own test report.'

  //Tercera seccion
  divContent.children[2].children[1].remove();//br
  divContent.children[2].children[1].remove();//breadcrumb

  //Cuarta seccion
  divContent.children[3].children[2].remove();//breadcrumb

  //Tablas
  const tablas = divContent.children[3].getElementsByTagName('section');
  divContent.children[3].children[1].appendChild(document.createElement('hr'));
  for (let i = 2; i < 2 + tablas.length; i++) {
    divContent.children[3].children[i].appendChild(document.createElement('br'));
    let hijos = divContent.children[3].children[i].children[2].children[0].childElementCount;//filas en la tabla
    for (let j = 0; j < hijos; j++) {
      divContent.children[3].children[i].children[2].children[0].children[j].children[0].remove();
    }
  }

  //Ultima section
  if (divContent.children[4]) {
    divContent.children[4].remove();
  }

  //ESTILOS
  //Convertimos todas las tablas al estilo de Bootstrap
  const table_primera = Array.from(divContent.children[1].getElementsByTagName('table'));
  table_primera.forEach(x => x.setAttribute('class', 'table table-striped'));

  const table_segunda = Array.from(divContent.children[2].getElementsByTagName('table'));
  table_segunda.forEach(x => x.setAttribute('class', 'table table-striped'));

  const tables_tercera = Array.from(divContent.children[3].getElementsByTagName('table'));
  tables_tercera.forEach(x => x.setAttribute('class', 'table table-striped table-bordered'));
};
