package de.muenchen.eh.claim.xta.transport;

import jakarta.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringDataSource implements DataSource {

    private final String content;
    private final String contentType;
    private final String name;

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content.getBytes());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("OutputStream not supported");
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
