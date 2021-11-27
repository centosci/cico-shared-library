package org.centos.ci.pipeline.lib

class Constants {
    public static final List<String> ACTIVE_FEDORAS = Collections.unmodifiableList(Arrays.asList("f30", "f31","f32", "f33", "f34", "f35", "latest"));
    public static final List<String> ACTIVE_ELS = Collections.unmodifiableList(Arrays.asList("el6","el7", "el8"));
}
