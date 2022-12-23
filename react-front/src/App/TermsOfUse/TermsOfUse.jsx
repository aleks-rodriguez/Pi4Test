import React from 'react';
import { useTranslation } from 'react-i18next';

function TermsOfService() {
  const { i18n } = useTranslation();

  const lang = i18n.language;

  return (
    <div>
      {lang === 'es'
        ? (
          <>
            <h2 style={{ textAlign: 'center' }}>
              <strong>Términos y Condiciones de Uso</strong>
            </h2>
            <p>&nbsp;</p>
            <p><strong>INFORMACIÓN RELEVANTE</strong></p>
            <p>
              El uso de nuestros servicios implicará que usted ha leído y aceptado los Términos y
              Condiciones de Uso en el presente documento.
            </p>
            <p>
              El usuario puede elegir y cambiar la clave para su acceso de administración de
              la cuenta en cualquier momento, en caso de que se haya registrado y que sea
              necesario para la compra de alguno de nuestros productos.
              Pi4Test no asume la responsabilidad en caso de que se entregue dicha clave a terceros.
            </p>
            <p><strong>LICENCIA</strong></p>
            <p>
              Pi4Test &nbsp; a través de su sitio web concede una licencia para que los
              usuarios utilicen&nbsp; este sitio web de
              acuerdo a los Términos y Condiciones que se describen en este documento.
            </p>
            <p><strong>USO NO AUTORIZADO</strong></p>
            <p>
              En caso de que aplique (para venta de software, u otro producto de
              diseño y programación) usted no puede colocar uno de nuestros productos,
              modificado o sin modificar, en un CD, sitio web o ningún otro medio y
              ofrecerlos para la redistribución o la reventa de ningún tipo.
            </p>
            <p>
              En caso de que se incumplan dichos términos, o si los administradores del sitio web
              detectan algún tipo de actividad fraudulenta o sospechosa, su cuenta de usuario
              puede ser bloqueada.
            </p>
            <p><strong>PROPIEDAD</strong></p>
            <p>
              Usted no puede declarar propiedad intelectual o exclusiva a ninguno de
              nuestros productos, modificado o sin modificar. Todos los productos son
              propiedad de los proveedores del contenido. En caso de que no se
              especifique lo contrario, nuestros productos se proporciona sin ningún
              tipo de garantía, expresa o implícita.
            </p>
            <p><strong>PRIVACIDAD</strong></p>
            <p>
              Este sitio web garantiza que
              la información personal que usted envía cuenta con la seguridad necesaria
              de acuerdo a las leyes Europeas. Todos los datos personales que usted
              proporciona se alojan en servidores Europeos. Los
              datos ingresados por usuario no serán entregados a terceros, salvo que deba
              ser revelada en cumplimiento a una orden judicial o requerimientos legales.
              Los datos podrán ser eliminados accediendo a su cuenta y solitando su borrado.
              Una vez realizado, no quedará ninguna información en nuestros servidores.
              Este sitio no usa cookies.
            </p>
            <p>
              Pi4Test se  reserva los derechos de cambiar o de modificar estos términos sin
              previo aviso.
            </p>

          </>
        )
        : (
          <>
            <h2 style={{ textAlign: 'center' }}>
              <strong>Terms & Conditions of Use</strong>
            </h2>
            <p>&nbsp;</p>
            <p><strong>RELEVANT INFORMATION</strong></p>
            <p>
              By using our services, you signify that you have
              read and accepted the Terms and Conditions of Use Conditions of Use in the
              present document.
            </p>
            <p>
              The user can choose and change the password for his account management access at any
              time, in case he has registered .
              Pi4Test does not assume liability in the event
              of such a key being given to a third party.
            </p>
            <p><strong>LICENSE</strong></p>
            <p>
              Pi4Test
              &nbsp;through its website grants a licence for users to use this website in accordance
              with the Terms and Conditions described in this document.
            </p>
            <p><strong>UNAUTHORIZED USE</strong></p>
            <p>
              If applicable you may not place our products modified or unmodified,
              on a CD, website or any other
              media and offer them for redistribution or resale of any kind.
            </p>
            <p>
              In the event of a breach of these terms, or if the website administrators
              detect any fraudulent or suspicious activity, your user account will be terminated.
            </p>
            <p><strong>OWNERSHIP</strong></p>
            <p>
              You may not claim intellectual or exclusive ownership of any of our products,
              modified or unmodified. All products are the property of the content providers.
              Where not otherwise specified, our products are provided without any warranty
              of any kind, express or implied.
            </p>
            <p><strong>PRIVACY</strong></p>
            <p>
              This website
              ensures that the personal information you submit is secure in accordance with
              European law.
              All personal data you provide is hosted on European servers.
              The data entered by you will not be disclosed to third parties,
              unless it must be disclosed in compliance with a court order or legal requirements.
              The data can be deleted by logging into your account and requesting deletion.
              Once this is done, no information will remain on our servers.
              This site does not use cookies.
            </p>
            <p>
              Pi4Test reserves the right to change or modify these terms without notice.
            </p>
          </>
        )}
    </div>
  );
}

export default TermsOfService;
