package org.minjay.allitebooks.Controller;

import org.minjay.allitebooks.AllitebooksCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/capture")
public class CaptureController {

    private final AllitebooksCapture capture;

    @Autowired
    public CaptureController(AllitebooksCapture capture) {
        this.capture = capture;
    }

    @RequestMapping(value = "/catalogs", method = RequestMethod.GET)
    public ResponseEntity<String> captureCatalog() {
        try {
            capture.getCatalog();
            return ResponseEntity.ok("capture catalog success");
        } catch (Exception ex) {
            return ResponseEntity.ok("capture catalog throw exception :" + ex.getLocalizedMessage());
        }
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public ResponseEntity<String> getBooks() {
        try {
            capture.getBooks();
            return ResponseEntity.ok("capture book names success");
        } catch (Exception ex) {
            return ResponseEntity.ok("capture book names throw exception :" + ex.getLocalizedMessage());
        }

    }
}
