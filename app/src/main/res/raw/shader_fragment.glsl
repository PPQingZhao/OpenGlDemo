precision mediump float;
varying vec2 ft_Position;
uniform sampler2D sTexture;// 输入的材质　(单像素,不透明灰度)
void main(){
    // 　取出材质sTexture中,坐标ft_Posiotion位置的像素
    gl_FragColor = texture2D(sTexture, ft_Position);
}
