package ocr;


import com.thc.platform.Application;
import com.thc.platform.modules.ocr.bean.OcrInvokeStatistical;
import com.thc.platform.modules.ocr.biz.IOcrRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class OcrRecordTest {

    @Resource
    private IOcrRecordService ocrRecordService;


    @Test
    public void getCountByTenantIdAndOrgId() {
        List<OcrInvokeStatistical> result = ocrRecordService.getCountByTenantIdAndOrgId("2020-02-05");
        result.forEach(System.out::println);
    }


}
