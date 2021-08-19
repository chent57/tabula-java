package chent57;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * 主要功能：1.验证签名过的pdf内容是否被篡改 2.验证签名有效性 3.数字证书有效性（需要通过根证书进行验证）
 * 真实性、有效性
 */
public class TestPdfVerification {

    /**
     * 验证pdf签名是否正确，会有三种情况，一种是验证抛异常了（这种情况看成是验证签名失败），第二种是验证成功， 第三种是验证失败
     *
     */
    @Test
    public void testFalsifyVerify() {
        // 1. 读取文件
        File pdf = new File("D:\\项目\\PDF验签\\acw-ug-doublesigned-modify.pdf");

        try {
            // 2. pdfbox读取PDDocument
            PDDocument pdfDocument = PDDocument.load(pdf);

            List<PDSignature> pdSignatureList = pdfDocument.getSignatureDictionaries();

            PDSignature pdSignature = pdfDocument.getSignatureDictionaries().get(0);
            byte[] pdfByte;
            pdfByte = IOUtils.toByteArray(new FileInputStream(pdf));

            byte[] signatureAsBytes = pdSignature.getContents(pdfByte);
            byte[] signedContentAsBytes = pdSignature.getSignedContent(pdfByte);

            CMSSignedData cms = new CMSSignedData(new CMSProcessableByteArray(signedContentAsBytes), signatureAsBytes);
            SignerInformation signerInfo = (SignerInformation) cms.getSignerInfos().getSigners().iterator().next();
            X509CertificateHolder cert = (X509CertificateHolder) cms.getCertificates().getMatches(signerInfo.getSID())
                    .iterator().next();
            SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider(new BouncyCastleProvider()).build(cert);

            boolean verifyRt = signerInfo.verify(verifier);
            System.out.println("result: " + verifyRt);
        } catch (IOException | CMSException e) {
            System.out.println("result:" + false);
            e.printStackTrace();
        } catch (CertificateException | OperatorCreationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除带签名PDF文件的一页内容，此操作会导致签名失效，adobe reader表现是签名是无效的，代码中验证会出现解析hex的异常
     * @throws IOException IoException
     */
    @Test
    public void testRemovePage() throws IOException {
        File pdf = new File("D:\\项目\\PDF验签\\协议签章样例.pdf");
        PDDocument pdfDocument = PDDocument.load(pdf);
        pdfDocument.removePage(2);
        pdfDocument.save("D:\\项目\\PDF验签\\协议签章样例-removepage.pdf");
    }

}
