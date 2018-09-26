package me.exrates.openapi.models.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.exrates.openapi.models.chart.ChartResolution;

import java.io.IOException;

public class ChartResolutionSerializer extends JsonSerializer<ChartResolution> {

    @Override
    public void serialize(ChartResolution value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
