// File: src/main/java/com/example/demo/BinderControllerAdvice.java
//
// Conceptual remediation for CVE-2022-22965 (Spring4Shell): a global
// WebDataBinder configuration that denies binding of the "class"/"module"
// properties the exploit relies on to reach the Tomcat ClassLoader.
//
// NOT BUILT OR TESTED. Vulhub's lab environment ships a pre-built image
// with no application source access, so this class was written as the
// documented fix but never compiled into a running container. Treat it as
// a reference implementation of the standard CVE-2022-22965 mitigation,
// not as verified-working code from this lab.
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class BinderControllerAdvice {

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        // deny vulnerable class properties
        String[] denylist = new String[]{
            "class.*", "Class.*", "*.class.*", "*.Class.*"
        };
        dataBinder.setDisallowedFields(denylist);
    }
}
