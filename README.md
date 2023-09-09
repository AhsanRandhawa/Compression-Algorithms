# CompressionAlgorithms
To use the Huffman Compressor and Decompressor:
    1. Compile HuffCode.java by running javac HuffCode.java
    2. Generate the encodings to be used by running java HuffCode [path/to/file or path/to/directory] 
        (The encodings can be generated from all text files found in a folder or a single text file)
    3. To compress:
        - Compile huffC.java by running javac huffC.java
        - Generate the compressed file/files by running java huffC  [path/to/file or path/to/directory] [path/to/output/directory]
           (The compressor can compress all text files found in a directory or a single file. There's an optional second argument
            that can be used to output the compressed file/files to a specific directory.)
    4. To decompress:
        - Compile huffD.java by running javac huffD.java
        - Generate the decompressed file/files by running java huffd  [path/to/file or path/to/directory] [path/to/output/directory]
           (The decompressor can decompress all compressed found in a directory or a single file. There's an optional second argument
            that can be used to output the decompressed file/files to a specific directory.)