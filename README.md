# IndagoIp

App Android desarrollada por [David Álvarez](https://github.com/davidalvarezcastro/) para la práctica de Sistemas Ubicuos del Máster en Ingeniería Informática de la Universidad de Burgos.

El principal objetivo es el desarrollo de una aplicación en Android para permitir al usuario consultar la posición en un mapa dado un host.

Requisitos funcionales:

- Visualización por host
- Visualización por información extraída de página web
- Settings para seleccionar el proveedor de mapas y la activación del guardado de las consultas realizadas
- Gestión del acelerómetro para cambiar entre proveedores

Requisitos no funcionales:

- Compatible para mínimo API 17, y versiones posteriores.
- Compatibile para dispositivos normal ldpi, xhdpi y tablets de x-large mdpi.

En la carpeta `pictures` se muestran una serie de capturas de la aplicacion.

---

## Funcionalidades

La aplicación está formada por varias pantallas y submenús utilizadas para permitir a la aplicació cumplir con las funcionalidades especificadas. Estas vistas se describen a continuación, sin entrar demasiado en detalles respecto a la interfaz gráfica.

### **Main**

Se presenta al usuario un campo de entrada de texto donde especificar el host o la IP y dos botones que ejecutan la funcionalidad de los dos casos de uso especificados.

Al pulsar sobre el botón _Localizar_ se lleva a cabo el Caso 1 y se muestra el mapa en la vista **_Visualización del mapa_**.

Al pulsar sobre el botón _Buscar enlaces_ se lleva a cabo el Caso 1 y se muestra el listado de los hosts encontrados en el código de la página web en la vista **_Listado de hosts_**.

En la parte superior se puede apreciar el _toolbar_ y el menú con diferentes opciones adicionales.

### **Listado de hosts**

Se muestra el listado de los hosts encontrados mediante un _ListView_. El usuario selecciona uno de los _items_ y se visualizan las coordenadas en el mapa (vista **_Visualización del mapa_**) siguiendo el mismo procedimiento que en el Caso 1 (se reutiliza la misma función).

### **Visualización del mapa**

Esta vista simplemente llama a los servicios de los proveedores de mapas con las latitudes y longitudes especificadas mediante el uso de un _WebView_.

En esta vista se visualiza un icono flotante que permite indicar al usuario la posibilidad de hacer uso del acelerómetro para cambiar entre los proveedores de mapas. Esta opción está deshabilitada en caso de previsualizar una consulta ya realizada (opción _Logs_ del menú superior).

### **Menú (_toolbar_)**

Menú que se encuentra en la parte superior de la aplicación con 3 opciones adicionales de soporte al usuario.

#### _Settings_

Se permite al usuario seleccionar el proveedor de mapas para llevar a cabo la visualización de la localización y la opción de almacenar en una base de datos las consultas llevadas a cabo en la aplicación.

#### _Logs_

Vista de ayuda para el desarrollador que permite visualizar el listado de consultas realizadas. El principal objetivo es asegurarse de que las consultas se están guardando correctamente; en una aplicación final, está opción no aparecería.

De forma adicional, se permite al usuario previsualizar en el mapa la consulta realizada (solo aquellas realiazadas contra los proveedores de servicios).

#### _Acerca de_

Se muestra información relativa al desarrollador como el nombre, ciudad de origen, foto de perfil así como enlaces de interés: github, facebook o la posibilidad de enviarle un email.

---

## Consideraciones

- Se han realiza diferentes pruebas de interacción y comportamiento en diferentes dispositivos para asegurar su fiabilidad y robustez.

- Se permite al usuario utilizar el acelerómetro solo para cambiar de proveedor en la vista de **_Visualización del mapa_**, en caso de cambiar de proveedor predefinido para las búsquedas, se dispone de la pestaña **_Settings_** en el menú. Esta decisión se ha tomado debido a que se ha realizado una prueba con un grupo de 4 usuarios finales que encontraron innecesario agitar el smartphone para cambiar de proveedor, debido a que simplemente tenian que dirigirse a la vista de _Settings_; en cambio, encontraban útil el hecho de cambiar de proveedor de mapas en esa vista para no cambiar de pestaña.

- La sensibilidad para detectar la agitación se puede modificar, por defecto es necesario agitar un par de veces el dispositivo. Para el emulador, la mejor opción es mover el dispositivo en el eje 'x' mediante latigazos rápidos de ratón, de derecha a izquierda; en un dispositivo real, el proceso es más sencillo.

- Se ha decidio almacenar en los logs de las peticiones tanto las peticiones a los proveedore de mapas como las propias realizadas por los usuarios en la aplicación (actividad principal); para ello, se han clasificado en tipos en la base de datos.

- En la vista con la lista de peticiones, se permite previsualizar la petición llevada a cabo; en este caso, la opción de cambiar de proveedor de servicios no está habilitada.

- No se ha considerado necesario cambiar la interacción entre la aplicación y el usuario (interfaz gráfica) en diferentes tipos de dispositivos (_tablet_ y _smartphone_).

- Para llevar a cabo el Caso de Uso II se había considerado utilizar la librería [JSoup](https://jsoup.org/), con esta librería se podría parsear el HTML del host especificado de forma rápida y sencilla. Se han utilizado los módulos especificados en el Guión de Prácticas, con el problema intrínseco de utilizar expresiones regulares para detectar hostnames. La expresión regular escogida, no consigue detectar la totalidad de los `hostnames` e identifica ciertas cadenas de manera errónea; una solución habría sido mirar el contenido de los atributos `href` y `src` (de esta manera se conseguían algunos `hostnames` adicionales), pero creo que la idea era detectar **todo** los `hostnames` referenciados en el HTML (incluidos comentarios, textos adicionales y otros).

## Trabajo futuro y mejoras

- Cambiar la vista principal de entrada de datos en las tablet, utilizando fragments para poder visualizar el mapa dentro de la propia Actividad.

- Opción de cambiar a otro proveedor de servicios en caso de previsualizar una petición realizada por el usuario (vista de _logs_).

---

[**Contactar Desarrollador**](mailto:dac1005@alu.ubu.es?subject=[SAL]%20Duda%20Proyecto%20Android)
