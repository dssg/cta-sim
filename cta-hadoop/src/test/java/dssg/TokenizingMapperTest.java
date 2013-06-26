package dssg;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TokenizingMapperTest {
    @Mock 
    private Mapper<LongWritable, Text, Text, IntWritable>.Context context;
    private TokenizingMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new TokenizingMapper();
    }

    @Test
    public void testMap() throws IOException, InterruptedException {
        mapper.map(new LongWritable(0), new Text("foo bar bar"), context);
        
        verify(context, times(1)).write(new Text("foo"), new IntWritable(1));
        verify(context, times(2)).write(new Text("bar"), new IntWritable(1));
        
        verifyNoMoreInteractions(context);
    }
}
