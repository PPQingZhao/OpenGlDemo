precision mediump float;
varying vec2 ft_Position;
uniform sampler2D sTexture;
void main(){
    //　取出　ft_position　坐标对应的rgba， 是向量存储的
    lowp vec4 textureColor = texture2D(sTexture, ft_Position);
    //　对textureColor进行处理  (黑白)
    float gray = textureColor.r*0.2125 + textureColor.g*0.7154 + textureColor.b*0.0721;
    gl_FragColor = vec4(gray, gray, gray, textureColor.w);
}
