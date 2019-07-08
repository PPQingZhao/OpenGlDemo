precision mediump float;
varying vec2 ft_Position;
uniform sampler2D sTexture;
void main(){
    lowp vec4 textureColor = texture2D(sTexture, ft_Position);
    //　亮度处理: rgb 分别减0.5
    gl_FragColor = vec4(textureColor.rgb + vec3(-0.5), textureColor.w);
}
