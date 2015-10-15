<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="index.pageTitle" /> </title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
  </head>
  <body class="environment index">
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="registerinfo" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
	    
		  <div class="GUI_pageContainer">
      
        <h2 class="documenttitle">Tietosuojaseloste - eDelfoi</h2>
        
        <div class="documentContentContainer">
        
        <h3>1. Rekisterin pitäjä</h3>

        <p>
        Otavan Opisto<br/>
        Mikkelin kaupunki<br/>
        y-tunnus: 0165116-3
        </p>

        <h3>2. Yhteyshenkilö</h3>

        <p>
        Ville Venäläinen<br/>
        Otavantie 2 b<br/>
        50670 OTAVA<br/>
        puh. 044 794 5102
        </p>

        <h3>3. Rekisterin nimi</h3>

        <p>
        eDelfoi -tietosuojaseloste<br/>
        </p>
        
        <h3>4. Henkilötietojen käsittelyn tarkoitus</h3>

        <p>
        Rekisteri sovelluksen käyttäjistä ja näiden käyttöoikeuksista<br/>
        Käyttäjien välinen viestintä sähköpostin välityksellä
        </p>

        <h3>5. Rekisterin tietosisältö</h3>

        <p>
        Etunimi<br/>
        Sukunimi<br/>
        Kutsumanimi<br/>
        Sähköpostiosoite<br/>
        Salasana (käyttäjille, jotka eivät halua tunnistautua Facebookin, Twitterin tai Googlen kautta)<br/>
        Paneelit, joissa käyttäjä on mukana sekä rooli, jossa käyttäjä paneelissa toimii (manageri tai panelisti)
        </p>

        <h3>6. Säännönmukaiset  tietolähteet</h3>

        <p>Käyttäjä täyttää lomakkeelle tiedot itse</p>

        <h3>7. Säännönmukaiset tietojen luovutukset</h3>

        <p>Ei luovuteta tai käytetä ohjelman ulkopuolella</p>

        <h3>8. Rekisterin suojauksen periaatteet</h3>

        <p>Henkilötiedot suojataan asiattomalta pääsyltä ja laittomalta
käsittelyltä (esim. hävittäminen, muuttaminen tai luovuttaminen).
Salassa pidettävien ja arkaluonteisten tietojen suojaamiseen
kiinnitetään erityistä huomiota.</p>

        <p>Suojaus perustuu järjestelmätasolla valvottujen käyttäjäoikeuksien
käyttäjätileihin. Tietokanta varmuuskopioidaan maantieteellisesti
erillään sijaitsevaan konesaliin kiintolevypohjaiseen
varmistusjärjestelmään. Levytallennusjärjestelmästä tallennetaan
tiedot vielä varmuuskopiointinauhoille kolmanteen, edelleen
maantieteellisesti erillään sijaitsevaan paikkaan.
Varmistusnauha-asema sijaitsee lukitussa murtovalvonnan alaisena
olevassa tilassa. Nauhat säilytetään kassakaapissa.</p>

        <h3>9. Rekisteröidyn tarkastusoikeus</h3>

        <p>Rekisteröidyllä on oikeus tarkastaa itseään koskevat rekisterin tiedot.</p>

        <p>Tarkastuspyyntö tehdään henkilökohtaisen käynnin yhteydessä tai
omakätisesti allekirjoitetulla tai muulla luotettavalla tavalla
varmennetulla asiakirjalla. Tarkastuspyyntö kohdistetaan rekisterin
yhteyshenkilölle.</p>

        <h3>10. Tiedon korjaaminen</h3>

        <p>Rekisterissä olevien virheellisten, puutteellisten tai vanhentuneiden
henkilötietojen korjauspyynnöt voidaan osoittaa rekisterin
yhteyshenkilölle. Henkilöllisyytensä varmistaneen henkilön vaatimat
rekisteriä koskevat korjaukset ja muut muutokset tehdään viipymättä.</p>

        <p>Jollei muutospyyntöä katsota perustelluksi, annetaan muutospyynnön
esittäjälle kirjallinen todistus, jossa selvitetään syyt miksi
muutosvaatimusta ei olla hyväksytty. Rekisteröity voi saattaa asian
tietosuojavaltuutetun käsiteltäväksi.</p>

	      </div>
	      <div class="clearBoth"></div>
	    </div>
	    
    </div>
    
    <jsp:include page="/jsp/templates/index_footer.jsp"></jsp:include>
    
  </body>
</html>