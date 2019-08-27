package com.github.mtesmct.rieau.api.depositaire.infra.adau.xml;

import static org.junit.Assert.assertThat;

import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.ADAUMessage;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.XmlService;
import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.XmlUnmarshallException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XPathXmlServiceTests {
    @Autowired
    private XmlService xmlService;

    @Test
    public void unmarshallMessageTest() throws XmlUnmarshallException {
        ADAUMessage message = this.xmlService.unmarshall("src/test/fixtures/message.xml");
        assertThat(message, notNullValue());
        assertThat(message.getCode(), is("cerfa_13703_DPMI"));
        assertThat(message.getId(), is("A-9-X3UGO4V7"));
        assertThat(message.getCerfaFileName(), is("A-9-X3UGO4V7-DAUA-doc-cerfa_13703_DPMI-1-2.pdf"));
        assertThat(message.getDate(), is("2019-04-03T16:26:20.790+02:00"));
    }
}