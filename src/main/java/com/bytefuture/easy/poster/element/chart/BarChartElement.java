package com.bytefuture.easy.poster.element.chart;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.element.chart.bar.BarChartLabelRenderer;
import com.bytefuture.easy.poster.element.chart.bar.BarChartLayoutCalculator;
import com.bytefuture.easy.poster.element.chart.bar.BarChartRangeResolver;
import com.bytefuture.easy.poster.element.chart.base.ChartValueRange;
import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 闂傚倸鍊风粈渚€骞栭锔藉剹濞达絽婀遍々鍙夈亜閹哄秶鍔嶉柛娆忕箻閺岀喖姊荤€靛壊妲梺缁樺姇閿曨亪寮婚悢纰辨晬闁糕剝顨呴弳娆戠磼濡や焦鐨戠紒杈ㄦ尰閹峰懘骞嬮幒鎴炲創濠电姵顔栭崰鏇㈠础閹惰棄鏄ラ柍?
 * 闂傚倸鍊烽悞锕€顪冮崹顕呯劷闁秆勵殔缁€澶愭倵閿濆骸澧插┑顔挎珪閵囧嫰骞掗幋婵愪患闂佸湱鎳撻悥濂稿蓟閺囩喓绡€闊洦绋掗宥夋倵鐟欏嫭绀冨┑鐐诧躬瀵鎮㈤懖鈺佸絾闂佸湱绮敮鎺撴叏瀹ュ鈷戦柣鎾虫捣娴犳盯鏌涢妸銉﹁础婵″弶鍔欓獮鎺楀箠瀹曞洤鏋涚€规洦鍋婂畷鐔碱敇閻愯尙鐣遍梻鍌氬€风粈渚€骞夐敍鍕殰闁圭儤鍤﹀☉妯锋婵﹩鍓欓悘濠囨倵楠炲灝鍔氭い锔诲灦閹锋垿鎮㈤悡搴ｉ獓闂佸壊鍋呯喊宥呪枍閸涱垳绠旈柟杈鹃檮閳锋垿鏌熼懖鈺佷粶闁告梹鐟╅弻娑㈠即閻曞倽鈧潡鏌涢埞鎯т壕婵＄偑鍊栭崹鐓庘枖閺団懞鍥晝閸屾稓鍘介梺鍝勭Р閸庨亶鎮橀懠顑藉亾鐟欏嫭绌跨紓宥勭窔楠炲啴鍩℃担鐑樻闂佹悶鍎滈崘顓炲帯闂傚倸鍊烽懗鍓佸垝椤栫偐鈧箓宕煎┑鍐╃亙濠电偞鍨堕埣銈夋偡閹锋梻鍠栭幊锟犲Χ鎼淬垻銈梻鍌欑窔濞佳囁囬銏犵？闂侇剙绉寸粻浼存煏閸繍妲归柣鎾跺枔閹插憡鎯旈妸銉э紵闂佽鍎煎Λ鍕垂閸岀偛绠圭紒顔炬嚀椤曪繝鏌熼悜姗嗘當婵☆偅锕㈤幃褰掑箒閹烘垵顬堥梺璇叉禋閸ｏ絽顫忓ú顏勫窛濠电姴瀚崳顔界節濞堝灝鏋ら柡浣割煼楠炲啴鏁撻悩鑼啋濡炪倖妫佸Λ鍕瑜版帗鐓熼柣妯煎劋椤忕娀鏌涙惔娑樷偓妤冨垝鐎ｎ喖閱囬柕澶涘閸?
 *
 * @author biaoy
 * @since 2026/04/11
 */
public class BarChartElement extends AbstractDimensionElement<BarChartElement> {

    private static final BarChartLayoutCalculator LAYOUT_CALCULATOR = new BarChartLayoutCalculator();

    private static final BarChartLabelRenderer LABEL_RENDERER = new BarChartLabelRenderer();

    private static final BarChartRangeResolver RANGE_RESOLVER = new BarChartRangeResolver();

    /**
     * 濠电姵顔栭崰妤冩暜濡ゅ啰鐭欓柟鐑樸仜閳ь剨绠撳畷濂稿Ψ椤旇姤娅嶉梻浣哄帶椤洟宕愰弴鐘亾閸偆鍙€闁诡喖缍婂畷鍫曞Ω閵壯呫偡闂備浇顕х换鎴︽偡閳轰緡娼栨繛宸簻缁狙囨偣閹帒澹夐柕澶堝労閻斿棝鏌ｉ悢宄扮盎闂婎剦鍓熼弻鈩冩媴閸濄儛銈吤归悪鍛暤闁诡喗鐟╅、妤呭焵椤掑倹顫曢柨鏃傛櫕缁犻箖鏌熺€电浠︾€规洖瀚换娑㈠椽閸愵亞袦闂佸搫鑻悧鎾荤嵁濮椻偓椤㈡瑩鎳栭埡濠冃㈠┑鐘茬棄閺夊簱鍋撻幇鏉跨；闁规儳鐏堥崑鎾舵喆閸曨剛顦梺鍝ュУ閻楃娀鐛崘顕呮晜闁割偒鍋呴弲銏＄箾鏉堝墽鍒伴柟鑺ョ矊铻ｉ柛鏇ㄥ灡閻撶喖骞栧ǎ顒€鈧倕顭囬幇顔瑰亾閸忓浜鹃梺褰掓？缁秹鏌ㄧ€ｎ剛鎳濋梺閫炲苯澧存鐐诧工閳规垹鈧綆浜為悡鎴炵節閵忥絾纭炬い鎴炲姇鍗辨い鏍仦閳锋垿鏌涢敂璇插季闁绘帞鍋撻妵鍕晜閻愵剚姣堟繝纰樷偓宕囨憼闁诡垱妫冩慨鈧柍銉ュ暱婵℃娊姊绘繝搴′簻婵炶绠撻獮鎰節濮橆剛锛熼梺鍝勬储閸ㄦ椽鎮￠悢鍝ョ瘈濠电姴鍊块妤呮煟韫囨洖鏋庨柍瑙勫灴閹瑩宕ｆ径濠冪亷闂備浇顕栭崰鏇犲垝濞嗘劒绻嗛柟闂寸劍閺呮煡鏌涚仦鍓х叝濠?
     */
    private static final List<Color> DEFAULT_PALETTE = Arrays.asList(
            new Color(72, 133, 237),
            new Color(234, 67, 53),
            new Color(52, 168, 83),
            new Color(251, 188, 5),
            new Color(123, 97, 255),
            new Color(0, 172, 193)
    );

    /**
     * X 闂傚倷绀侀幖顐λ囬柆宥呯；婵炴垯鍩勯弫瀣亜閹捐泛校闁稿鐗楅妵鍕箛閸洘顎嶉梺绋块鐎涒晠濡甸崟顖氭闁割煈鍠楅崐顖炴⒑缁嬪尅宸ョ痪缁㈠幘濡叉劙骞掗幘宕囩獮闁诲函缍嗛崑鍡涘礄閿熺姵鈷戦柛鎾瑰皺閸樻稑霉濠婂懎浠辨鐐插暞鐎佃偐鍒掗崗澶婁壕闁挎洖鍊哥粻鎶芥煛閸愩劌浜炴俊鎻掑槻閳规垿鎮欑€涙ê闉嶉梺鐓庣秺缁犳牠銆侀弽顓炲窛婵烇綆鍏橀崑鎾诲川闁附鈻岄柣搴㈩問閸犳岸寮拠鑼殾闁绘挸瀵掗悡銉╂煕閹般劍娅嗘繛鍫熸濮婄粯绗熼埀顒€顭囪铻為柡鍐ㄥ€婚惌鍡椼€掑锝呬壕闂佽鍣换婵嗙暦閻旂⒈鏁囬柣鏂挎啞椤斿洭姊绘担渚劸闁哄牜鍓濊ぐ婊堟⒑缁嬫鍎愰柟姝屽吹缁參鎮㈤悡搴ｅ姦濡炪倖甯掔€氼剛澹曢崸妤佺厵缂備降鍨归弸鐔兼煃闁垮娴柡灞炬礃缁绘稖顦查柟鍐茬箻楠炲棝鏁愭径瀣ф嫼?
     */
    private final List<String> categories = new ArrayList<String>();

    /**
     * 闂傚倸鍊烽悞锕傚箖閸洖纾块柟缁樺笧閺嗭附鎱ㄥ璇蹭壕婵犵鍓濋幐鍐茬暦濮椻偓椤㈡棃宕熼顐㈡闂備浇宕垫慨鏉懨洪敃浣典汗闁告劦鍠楅崐鑸电箾閸℃ɑ灏伴柣鎾寸☉闇夐柨婵嗘噺閹牊銇勯敐鍫濅汗闁逞屽墲椤骞愭搴㈩偨婵﹩鍓﹀鏍煕瑜庨〃鍛寸嵁閵忊€茬箚闁靛牆瀚ˇ锕傛煃瑜滈崗娆撳磹濠靛钃熼柕濞垮劗濡插牊淇婇婊呭笡闁圭兘浜跺鐑樺濞嗘帒鎮呴梺鍛婃尰缁诲嫮鍒掑▎鎰窞闁规澘鐏氶弲婊堟⒑缁洖澧茬紒瀣灴瀹曘垹鈻庨幘绮规嫼闂佸憡绋戦敃銈囩箔濮橆厹浜滈柕濞垮劵闊剛鈧娲橀〃鍛达綖濠婂牆鐒垫い鎺嗗亾閾荤偛銆掑锝呬壕闂佺硶鏂侀崑鎾愁渻閵堝棗绗掗柛濠傜秺瀹曨垶宕崝钘夌秺閺佹劖寰勫Ο娲绘闂備浇宕甸崰鍡涘垂閼姐倖顫曢柟鐑橆殢閺佸啴鏌曟径鍫濆姎闁诡垳鍋涢埞鎴︽倷鐠鸿櫣澶勯梺鍛婎殔閸熷潡顢氶敐澶樻晝闁挎繂鎷嬮崵銈夋⒑閸濆嫷妲归柛銊ョ埣椤㈡棃鍩勯崘顏嗩啎闂佺懓顕崑鐐典焊閵娾晜鐓曢悗锝庡亝鐏忣參鏌嶉挊澶樻█妤犵偞锕㈤、娑橆潩椤愩埄妫滈梻鍌欐祰椤曆兠归悜钘夋瀬闁归棿绀佺粻鐘绘煙閻愵剙鍔跺┑顔藉▕閺岋紕浠︾拠鎻掑閺?
     */
    private final List<BarChartSeries> seriesList = new ArrayList<BarChartSeries>();
    /**
     * 闂傚倸鍊峰ù鍥ь浖閵娾晜鍊块柨鏇楀亾妞ゎ厼鐏濋～婊堝焵椤掑嫬鏄ラ柍褜鍓氶妵鍕箳閸℃ぞ澹曢梻浣告啞閼归箖顢栨径鎰畺鐟滃繒鍒掑▎蹇婃瀻闁诡垎鍕靛悪闂傚倸顭崑鍕洪敃鍌氱闁归棿绶￠弫瀣喐閺冨牆钃熸繛鎴欏灪閺呮煡骞栨潏鍓у埌閻㈩垬鍔庣槐鎾存媴娴犲鎽电紓浣筋嚙閻楁捇鐛崘鈺冾浄閻庯綆鈧厸鏅犻弻鏇㈠醇濠靛洨鈹涙繝銏ｎ潐閿曘垹顫忛搹鍦煋闁糕剝顨呴鈺佲攽椤旇偐澧﹂柡宀嬬秮瀵粙鎮介悽鐬垵鈹戦悙纰樻嫛濞存粠浜滈锝夊醇閺囩偟顓洪梺缁樺姇濡﹥瀵奸崨瀛樷拻濞达綀濮ら妴鍐煟閹虹偟鐣辨い顓炵仢椤粓鍩€椤掑嫬鏄ラ柍褜鍓氶妵鍕箳閸℃ぞ澹曢梻浣告啞閼归箖顢栨径鎰畺闁靛鏅滈弲鏌ユ煕閵夈垺娅婇柍鐟扮箻濮婃椽宕ㄦ繝鍐ㄧ樂闂佸憡娲﹂崢钘夆枔閹间焦鈷掑ù锝呮啞閹牓鏌涙繝鍕电劸閻撱倝鏌ㄩ弴妤€浜鹃梺鎸庢磸閸ㄤ粙鐛澶樻晩缂備焦锚婵即姊绘担鍛婃儓婵炲眰鍔嶉幈銊╂偋閸埄娴勫┑鐐村灟閸ㄦ椽鎮?
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    /**
     * 闂傚倸鍊烽悞锕傚箖閸洖纾块柟缁樺笧閺嗭附鎱ㄥ璇蹭壕婵犵鍓濋幐鍐茬暦濮椻偓椤㈡瑩鎳栭埡濠冃為梻鍌欑閹测剝绗熷Δ鍛煑閹兼番鍔岄悿顕€鏌涢妷顔煎闁绘挻娲熼弻鐔兼焽閿曗偓婢ь垶鏌涢妶鍌氫壕缂傚倸鍊烽悞锕€顫忔繝姘獥闁规崘顕х粻鏌ユ煕閺囥劌浜愰柡鈧禒瀣厵闁诡垎鍐炬殺濡炪倧瀵岄崳锝咁潖閾忚鍏滈柛娑卞灣閸旂兘姊洪崨濠傚闁告柨閰ｅ畷銏ゎ敆閸曨兘鎷洪梺鍛婄箓鐎氼喖鐡繝鐢靛仜濡﹪宕ｉ崘顔嘉ラ柛灞剧〒椤╃兘鎮楅敐搴樺亾椤撴粌鍔氶棁澶愭煥濠靛棙鍣洪柛鐔哄仧缁辨帡宕掑鍗烆暫闂侀潧妫欑敮鎺楋綖濠靛鏁嗗璺猴工婢瑰嫰姊绘笟鈧埀顒傚仜閼活垶宕㈤幖浣圭厾闁告劖褰冮。鎶芥煕閹烘挸娴い銏＄洴閹瑩鎳犻鈧埀顒傚仱濮婅櫣鎷犻垾宕団偓濠氭煕閹伴潧鏋熸い鏂垮暙閳规垿鎮欓弶鎴犱桓闂佸湱顭堥幉锟犮€冮妷鈺佺濞达絺鍋撻柛銉㈡櫇绾惧吋淇婇婵嗗惞缂佸鐖煎娲閳轰胶妲ｉ梺鍛婎焾濡嫰鍩㈠澶婂嵆闁靛骏绱曢崢浠嬫⒑闂堟侗鐒鹃柛搴ㄤ憾椤㈡棃顢楅崒婊咃紲闂佸綊鍋婇崢楣冨Υ閹烘梻纾兼い鏃傗拡閸庡繘寮崼銉︾厱闁靛鍨抽崚鏉棵?
     */
    private Insets padding = new Insets(24, 24, 24, 24);
    /**
     * 闂傚倸鍊烽懗鍫曞储瑜斿畷顖炲锤濡も偓鐎氬銇勯幒鍡椾壕闂佸疇顕х粔鎾煝鎼淬劌绠婚柡澶嬪灦閻ゅ嫰姊洪懡銈呅㈡繛澹洤宸濇い鏍电稻鍟稿┑?null 闂傚倸鍊风粈渚€骞栭锕€鐤い鏍ㄧ啲閼板潡鏌涢敂璇插箻缂佺姵鏌ㄩ…璺ㄦ崉閾忓湱浼囩紒鐐劤閸氬绌辨繝鍥舵晬婵﹩鍘介崕鎾绘⒑缂佹ɑ灏伴柛銊ユ健瀵濡舵径濠勵槰闂佸憡鐟ラˇ浼村箯閸楃偐鏀介柣鎰级閸ｆ椽鏌ｉ悢鍙夋珕濞?
     */
    private Color backgroundColor;
    /**
     * 闂傚倸鍊烽懗鍫曪綖鐎ｎ喖绀嬫い鎰╁焺濞兼棃姊绘担铏瑰笡闁绘鍘惧濠囧箚瑜滈悞鑺ャ亜韫囨挾澧曠紒鐘电帛閵囧嫰寮撮悙鏉戞濠电姰鍨虹敮鈥愁潖濞差亝鍤冮柍鍝勫€稿▓宀勬⒑濞茶绨烽柛妤佸▕瀹曟椽鍩€?
     */
    private Color axisColor = new Color(85, 92, 110);
    /**
     * 缂傚倸鍊搁崐鎼佸磹閹间礁鐤い鏍仜閸ㄥ倿鏌涢锝嗙闁稿顑夐弻娑樷攽閸曨偄濮㈠┑鈽嗗亽閸ㄥ爼寮诲☉婊庢Ъ濡炪們鍔岀换妯侯嚕閹埇浜归柟鐑樻尵閸橀箖姊洪懡銈呮瀾濠㈢懓妫楅敃銏ゅ箻椤旂晫鍘?
     */
    private Color gridColor = new Color(225, 229, 238);
    /**
     * 闂傚倸鍊风粈渚€骞栭銈囩煋闁哄鍤氬ú顏嶆晣妞ゎ偒鍏橀崑鎾诲磼濞戞凹娴勯柣搴秵閳ь剦鍘归崕閬嶆箒闂佹寧绻傞幊蹇涘疮閻愮數纾界€广儱鎳愮粻濠氭煛鐏炵硶鍋撻幇浣告倯闂佸憡渚楅崰姘跺煘濞戙垺鈷掑ù锝呭槻瀹撳棗霉濠婂棙纭炬い鏇稻缁傛帞鈧綆浜為崐鐐烘⒑閸愬弶鎯堥柨鏇樺€栭幈銊﹀鐎涙ǚ鎷洪梻鍌氱墛缁嬫帡藟濞嗘垹纾奸柍褜鍓氶幏鍛嫚閵壯勫殌閾伙綁鏌ゆ慨鎰偓妤呭箠濠靛鐓熼幖鎼灣缁夌敻鏌涚€ｎ亝鍤囬柟顔筋殜椤㈡岸鍩€椤掑嫬钃熼柨鐔哄Т闁卞洦銇勯幇鈺佺仼闁冲嘲顑呴—鍐Χ閸愩劉鍋撻幘鑸殿偨婵娉涚粻鐘崇節闂堟稓澧旀繛宀婁邯閺屾稓浠︾紒銏＄暥婵?
     */
    private Color labelColor = new Color(71, 77, 92);
    /**
     * 闂傚倸鍊风粈渚€骞栭锔藉殣妞ゆ牗绺挎禍鐟邦熆鐠鸿櫣鐏辩紒鈧繝鍥ㄥ€甸柨婵嗛娴滄粎绱掗崜浣镐槐闁哄备鈧剚鍚嬮幖绮光偓宕囶啇闂備礁鎲￠弻锝夊磿閻㈢钃熼柍鈺佸暞婵挳鎮峰▎蹇擃伀闁告埊绻濆娲焻閻愯尪瀚板褜鍣ｉ弻娑橆潩椤掑鍓堕梺璇″枔閸庣敻寮幇鏉跨倞闁冲搫鎳夐弨鍝勨攽鎺抽崐褏寰婃禒瀣獥婵娉涚痪褏鎲搁悧鍫濈瑲闁绘挾濞€閺岋綁鎮㈢粙娆炬濠电姭鍋撻柟缁㈠枟閻?
     */
    private Color valueLabelColor = new Color(55, 60, 72);
    /**
     * 闂傚倸鍊烽悞锕傚箖閸洖纾块柟缁樺笧閺嗭附鎱ㄥ璇蹭壕婵犵鍓濋幐鍐茬暦濮椻偓椤㈡瑩鎮℃惔鈩冩殢闂傚倷鐒﹂幃鍫曞磿濞差亜绀堥柨鏇炲€稿Ч鍙夈亜閺嶎偄浠﹂柣鎾寸洴閺屾稓浠﹂崜褜鏆℃繝銏ｆ硾鐎氼厾鎹㈠┑瀣劦妞ゆ帊绀侀閬嶆煛婢跺娈?
     */
    private String title;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撳▓鍨灆闁告鍟块悾宄邦煥閸♀晜效濠电偛顕慨鐢告儎椤栫偛钃熸繛鎴欏灩鍞梺闈涱槶閸庡磭绮欒箛鏇犵＝濞达絿鏅崼顏堟煕鎼达紕浠涘ǎ?
     */
    private boolean showLegend = true;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撳▓鍨灆闁告鍟块悾宄邦煥閸♀晜效濠电偛顕慨鐢电矙閹烘挾鈹嶅┑鐘叉祩閺佸秹鏌ㄥ┑鍡楊伀闁烩晛閰ｅ铏规嫚閳ヨ櫕鐏嶇紓渚囧枛濞村嘲危閹版澘绫嶉柛顐ｇ箘椤撴椽姊虹紒妯哄婵炰匠鍥佸寮撮悩鐢碉紳婵炶揪绲块幊鎾剁矆閸愵喗鐓ラ柣鏃€娼欓崝锕傛煕閳规儳浜?
     */
    private boolean showGrid = true;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撳▓鍨灆闁告鍟块悾宄邦煥閸♀晜效濠电偛顕慨鐢告儎椤栫偛钃熼柨娑樺濞岊亪鏌﹀Ο渚Ш缂佸鍠氱槐鎾诲磼濮樻瘷銏°亜閹存繍妲告い鏇秮椤㈡洟鏁冮埀顒傜棯瑜旈獮鏍偓娑櫳戠亸顓灻瑰鍫㈢暫闁哄本绋栫粻娑橆潩椤戞寧鐫忛梻浣规偠閸斿繘宕戦幇顔筋潟闁圭儤鍨熷Σ鍫熶繆閵堝倸浜鹃柣搴㈣壘闁帮綁寮?
     */
    private boolean showValueLabel = true;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撳▓鍨灆闁告鍟块悾宄邦煥閸♀晜效濠电偛顕慨鐢告儎椤栫偛钃熸繛鎴炵矤濡插ジ姊洪崨濠冣拹闁绘濞€楠炲啴濡烽埡鍌涙珳闂佺硶鍓濋敋妞ゎ偀鈧枼鏀介柣鎴濇川缁夌敻鏌涜箛鏃撹€跨€?
     */
    private boolean showAxis = true;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撳▓鍨灆闁告鍟块悾宄邦煥閸♀晜效濠电偛顕慨鐢告儎椤栫偛钃熼柍銉﹀墯閸氬鏌涘☉鍗炴灈闁哄濮撮—鍐Χ韫囨洖顕辩紓渚囧枟閻燂附绌?
     */
    private boolean showTitle = true;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撶憴鍕８闁稿海鏁婚妴浣糕槈濮楀棛鍙嗛梺鍛婃处閸撴岸顢樿ぐ鎺撯拻濞达絿顭堥ˉ蹇涙煕鐎ｎ亜顏╅摶鐐翠繆閵堝懎顏ュù婊冪秺閺岋繝宕橀妸褍顣洪梺钘夊暟閸犳牕顕ｉ崼鏇為唶婵﹩鍘介悵鏃傜磼濡や礁鐏存慨濠冩そ楠炴牠鎮欓幓鎺懶戦梻浣侯焾椤戝棝骞戞笟鈧畷顖烆敍閻愭潙鈧灚绻涢崼婵堜虎闁哄绋撶槐鎺旀嫚閼碱剙鈪甸梺绯曟杹閸?
     */
    private boolean stacked = false;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撶憴鍕８闁稿海鏁婚妴浣糕槈濮楀棛鍙嗛梺鍛婃处閸撴岸顢樿ぐ鎺撯拻濞达絿鏅В鍥煕閺囥劌浜滃┑顔哄灲濮婃椽骞栭悙鎻掝瀳婵°倗濮甸悺鏇€傛ィ鍐┾拺闁告繂瀚埀顒冩閳绘棃寮撮姀鐘茬€繝鐢靛У绾板秹鎮￠弴鐔虹闁糕剝锚閻忋儵寮介敓鐘崇厾闁搞劍绋掑▍鍥煃瑜滈崜姘额敊閺嶎厼绐楅柡宥庡亝瀹曟煡鏌涢鐘插姎闁绘帒鐏氶妵鍕箳閸℃ぞ澹曢梻浣侯焾閿曘倝骞楀鍏撅綁骞囬弶璺吅闂佹寧娲嶉崑鎾绘偡濞嗘瑩妾柕鍥у瀵粙濡歌閺嗭繝姊虹粙鍖¤含婵炰匠鍥ㄧ畳闂備胶顭堢换妯何涢幐搴ｎ洸闁靛繈鍊栭悡鏇㈡煏閸繃顥犻柣顓熷笧閳ь剝顫夊ú妯兼崲閸喍绻嗛柟闂寸鎯熼悷婊冪箻椤㈡瑩骞掑Δ浣叉嫼缂備礁顑堝▔鏇犵不閻愮儤鐓曢幖娣妺閹查箖鏌熼鍝勫姦濠殿喒鍋撻梺闈涚墕閹虫劙鎮￠幋锔解拺缂備焦锕╅悞楣冩煃閵夛妇澧柟宄版嚇瀹曟粓骞撻幒鎿冨悪闂傚倸顭崑鍕洪妶澶樻晞闁稿瞼鍋涚壕?
     */
    private boolean percentStacked = false;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈傚亾濮樺崬鍘寸€规洝顫夌€靛ジ寮堕幋鐘垫毎濠电偞鎸婚崺鍐磻閹惧灈鍋撳▓鍨灆闁告鍟块悾宄邦煥閸♀晜效濠电偛顕慨鐢告儎椤栫偛钃熸繛鎴欏焺閺佸啴鏌曡箛濠冾潑婵☆偆鍋ら幃宄邦煥閸涱収鏆┑鈽嗗亜閸熸挳鎮伴鈧崺鈧い鎺戝閻撴盯鏌涢弴銊ヤ簼缂佸倸顑夐弻娑橆潩閿濆懍澹曢梻鍌氬€风粈渚€骞栭銈囩煋闁哄鍤氬ú顏勎╅柍鍝勶攻閺呫垽姊虹紒妯虹伇婵☆偄瀚换姘舵⒒娴ｄ警鐒鹃柡鍫墮椤繈濡歌閻挸鈹戦崒姘暈闁绘挻娲熼弻鐔煎箚瑜忛敍宥夋嚃閺嶎厽鐓熼柣鎰綑閸ゎ剟鏌涚€ｎ亝鍣界紒顔界懄瀵板嫰骞囬崹顐ｆ珝闂備胶绮弻銊╁箺濠婂牆姹查柣鎰劋閳锋垿鏌涘┑鍡楊伀濠⒀冨船闇夋繝濠傚閻帡鏌熼鍏煎仴鐎规洜鍠栭、娑樷槈閹烘挸鐦遍梻鍌欑閹测剝绗熷Δ鍛婵炲棙鎸搁悡婵嬬叓閸ャ劎鈯曢柣鎾跺枛閺屾洝绠涙繝鍐╃彇闂佸憡蓱缁海妲愰幒鎾寸秶闁靛ě鍛毇闂備胶鎳撻崲鏌ュ箠濡櫣鏆﹂梺顒€绉撮崡鎶芥煟濡椿鍟忛柛鐔风Ч濮婄粯鎷呮搴濊缂備焦鐓＄粻鏍箖閻戣棄鐓涢柛娑卞幖閸嬪秹姊洪棃娑氬婵炲眰鍔庢竟鏇㈡倷椤戝彞绨婚梺鍝勫暙閸婂摜鏁崜浣插亾濞堝灝鏋熷┑鐐诧躬瀵鈽夊Ο閿嬬€诲┑鐐叉閸ㄥ綊濡堕悜鑺モ拺?
     */
    private boolean showStackTotalLabel = false;
    /**
     * Y 闂傚倷绀侀幖顐λ囬柆宥呯；闁绘劕妯婇悞鑺ョ箾閸℃ɑ灏柡鍕╁劦閺屾洝绠涙繝鍌氣拤闂佺娴烽崰鏍蓟閵娾晛绫嶉柛銉仢閹惧绠鹃柛顐ｇ箘閸╋絾鎱ㄦ繝鍐┿仢妤犵偞鎹囬獮鎺懳旈崘銊︺仢缂傚倸鍊峰ù鍥ㄣ仈閸濄儲宕查柛顐犲劚缁犵姵绻涢幋娆忕仼缂佲偓閸愵喗鐓犵痪鏉垮船婢ь垶鏌ｉ敂鐣岀煁缂?2闂?
     */
    private int yAxisTickCount = 5;
    /**
     * 闂傚倸鍊烽懗鍫曪綖鐎ｎ喖绀嬫い鎰╁焺濞兼棃姊绘担铏瑰笡闁绘鍘惧濠囧箚瑜滈悞鑺ャ亜韫囨挻顥戦柛瀣崌閹棄鈻撶捄銊ュЪ婵犵數鍋涢悧蹇旂箾閳ь剟鏌熼绛嬫疁闁诡喚鍏橀弻鍥晜閻ｅ瞼鈼ラ梻?
     */
    private int axisStrokeWidth = 1;
    /**
     * 闂傚倸鍊风粈渚€骞栭銈囩煋闁哄鍤氬ú顏嶆晣妞ゎ偒鍏橀崑鎾诲磼閻愭潙浠奸柣蹇曞仧椤牊绂嶉悙顒傜闁瑰鍋熼幊鎰箾閸喐鈷掗柟鍙夋倐瀵爼骞愭惔鈥叉樊濠?
     */
    private int titleFontSize = 18;
    /**
     * 闂傚倸鍊烽懗鍫曪綖鐎ｎ喖绀嬫い鎰╁焺濞兼棃姊绘担铏瑰笡闁绘鍘惧濠囧箚瑜滈悞鑺ャ亜韫囨挻顥戦柛瀣尭閳藉鈻庡Ο鐓庡Ш闂備焦鎮堕崝蹇涘磻閹邦喗顫曢柟鐑樺灍濡插牊绻涢崱妤冪濠殿喓鍨藉娲倻閳轰緡娼氶梺鎼炲劀閸涱垰骞€闂傚倷绀侀幖顐︻敄閹版澘纾婚柍褜鍓熼弻?
     */
    private int labelFontSize = 12;
    /**
     * 闂傚倸鍊烽悞锕傚箖閸洖纾块弶鍫亖娴滃綊鏌ｉ幇顒備粵妞ゆ劒绮欓弻鏇熺珶椤栨浜鹃梺绋款儐閹告悂鍩ユ径濞炬瀻婵☆垵宕甸弳銉╂煟閻斿摜鐭嬫繝銏∶…鍥焺閸愌呯畾?
     */
    private int legendFontSize = 12;
    /**
     * 闂傚倸鍊风粈渚€骞栭锔藉殣妞ゆ牗绺挎禍鐟邦熆鐠鸿櫣鐏辩紒鈧繝鍥ㄥ€甸柨婵嗛閺嬫盯鏌＄€ｂ晝绐旈柡灞稿墲瀵板嫭绻濋崟顒傛闂備胶顭堥鍡涘箲閸パ呮殾婵°倕鎳忛崵鍐煃鏉炴媽鍏屽ù鐓庢川缁辨捇宕掑▎鎴濆缂備礁顑嗙敮鈥崇暦閺囥垹绠柤鎭掑劚娴犻亶鎮峰鍕叆闁伙絿鍏橀弫鎰緞婵犲倸娈ゆ俊鐐€栭崝妤呭窗鎼淬垻顩?
     */
    private int valueLabelFontSize = 11;
    /**
     * 闂傚倸鍊烽悞锕傚箖閸洖纾块弶鍫亖娴滃綊鏌ｉ幇顒備粵妞ゆ劒绮欓弻鏇熷緞濞戞粎顦版繛瀛樼矋缁挸顫忓ú顏嶆晝闁挎繂娲㈤埀顒佸笚閵囧嫰骞樼€涙ǜ浠㈠┑顔硷工椤嘲鐣烽幒鎴僵闁绘挸瀛╅宥嗙節閻㈤潧浠滈柣妤€锕畷褰掓偨闂堚晝绋忓┑鐘绘涧椤戝懐鐥閺岀喐娼忔ィ鍐╊€嶉柣蹇撶箲閻熲晛顫忔繝姘＜婵﹩鍏橀崑鎾寸節濮ｇ顦甸獮鍥敊閸撗冪婵犵數鍋為崹顖炲垂绾懐绠?
     */
    private int legendItemGap = 18;
    /**
     * 闂傚倸鍊烽悞锕傚箖閸洖纾块弶鍫亖娴滃綊鏌ｉ幇顒備粵妞ゆ劒绮欓弻鏇熷緞閸℃ɑ鐝曢梺鎼炲€曢惌鍌炲蓟閻旇櫣纾兼俊顖濇閻熸煡姊虹粙娆惧剭闁稿海鏁婚獮鍐ㄎ熺捄銊ф澑闂佽鍎抽悺銊╁磿閹炬剚娓婚柕鍫濇婢跺嫰鏌涘▎蹇撴殲濞?
     */
    private int legendMarkerSize = 10;
    /**
     * 闂傚倸鍊风粈渚€骞栭锔藉殣妞ゆ牗绺挎禍鐟邦熆鐠鸿櫣鐏辩紒鈧繝鍥ㄥ€甸柨婵嗛閺嬫稓绱掗埀顒勫磼濮ｎ厼缍婇弫鎰板炊閳哄倹鍟掗梻浣告憸閸犲酣骞婂鈧濠氭晲婢跺á鈺呮煏婢跺牆鍔村ù鐘冲劤閳规垿鎮欑€涙ê纾╅梺绯曟櫆閻楁洟顢氶妷鈺佺妞ゆ洖鎳忎簺濠碉紕鍋戦崐鏍ь潖閻熼偊娼栫憸鐗堝笒缁犳岸鏌￠崘銊у闁哄懏绮撻幃璺衡槈閹哄棗浜鹃柛蹇撴噹椤?
     */
    private int maxBarWidth = 56;
    /**
     * 闂傚倸鍊风粈渚€骞栭锔藉殣妞ゆ牗绺挎禍鐟邦熆鐠鸿櫣鐏辩紒鈧繝鍥ㄥ€甸柨婵嗛閺嬫稓绱掗埀顒勫磼濮ｎ厼缍婇弫鎰板炊閳哄倹鍟掗梻浣告憸閸犲酣骞婂鈧濠氭晲婢跺á鈺呮煏婢跺牆鍔村ù鐘冲劤閳规垿鎮欑€涙ê纰嶆繝鈷€鍐劉缂佸倸绉撮～銏犵暆閳ь剟鎮块埀顒勬⒑閻熸壆浠㈤柛鐕佸亰瀵煡顢楅崟顒傚幍闂佺厧婀辨晶妤勩亹瑜旈幃褰掑箛椤斿吋鐏堥梺绯曟杹閸?
     */
    private int minBarWidth = 6;
    /**
     * 闂傚倸鍊风粈渚€骞栭锔藉殣妞ゆ牗绺挎禍鐟邦熆鐠鸿櫣鐏辩紒鈧繝鍥ㄥ€甸柨婵嗛閺嬫盯鏌熼悷鎵煟闁哄本鐩崺鍕礃閵娿儲鐦撴繝鐢靛仜閻ㄧ兘鍩€椤掍礁澧柣鏂挎閺屾盯顢曢姀鈽嗘闂佸摜鍠愬妯款暰闂佸壊鍋侀崕鏌ユ偂? 闂傚倷娴囧畷鐢稿磻閻愮數鐭欓煫鍥ㄧ☉缁€澶愬箹濞ｎ剙濡煎鍛攽椤旂瓔鐒鹃柛鈺傜墵瀹曟洘鎯旈敐鍥紡闂佽鍨庣仦钘夊婵犵數鍋熼妴瀣崲濠靛钃?
     */
    private int barArc = 8;
    /**
     * 闂傚倸鍊风粈浣圭珶婵犲洤纾婚柛鏇ㄥ€犲☉妯滄棃宕熼埡鈧Ч妤呮⒑閸濆嫬鏆欓柣妤€妫涚划濠氬箣閿旂晫鍘搁梺鍛婃处閸嬪懐绮嬬€ｎ喗鐓曟慨姗堢到娴滈箖姊婚崒娆戭槮闁圭⒈鍋嗙划鏃堝级閹炽劍妞芥俊鎼佸煛婵犲啯娅岄梻鍌欑閻忔繈顢栭崶顒€鐓濋柡鍐ㄧ墕缁犺绻涢敐搴″闁诲浚鍣ｉ弻娑樷枎閹邦厾绋囬梻鍥ь樀閺屻劌鈹戦崱姗嗘！闁诲繐娴氶崑鍡欐閹烘鍋戦柛娑卞灣琚︽繝娈垮枛閿曘劌鐣烽悽绋跨劦妞ゆ帒瀚☉褔鏌熺拠褏绡€闁诡噣绠栭弻鍡楊吋閸″繑瀚介梻浣侯焾閺堫剛鍒掗悩鍙傛稒鎯旈敐鍥╋紲闂佸綊鍋婇崢鎯洪弶鎴旀斀闁斥晛鍟亸锔锯偓瑙勬礃鐢帡锝炲┑鍥ㄧ秶闁宠鍓︽禍顏勵潖濞差亜宸濆┑鐘插暙绾板秶绱撴担鍓叉Ц濠⒀冮叄楠炲啴顢氶埀顒勩€侀弴銏℃櫜闁搞儻绲鹃悘鍐╀繆閻愵亜鈧牠骞愭ィ鍐ㄧ獥闁规壆澧楅崑鈺傘亜閺嶎偄浠﹂柣鎾寸洴閺屾盯顢曢顫暗缂備降鍔岄…鐑藉蓟閿濆围闁告侗鍙庢导鍐ㄢ攽椤旂》姊楃紒顔界懃閻ｇ兘濡搁埡濠冩櫌闂侀€炲苯澧撮柛鈹垮灱缁犳稑鈽夊▎鎴濆箺婵＄偑鍊ら崑鎺楀礈濞嗘劒绻嗛柟闂寸劍閻?
     */
    private int stackTotalLabelGap = 6;
    /**
     * 濠电姷鏁告慨浼村垂婵傜鏄ラ柡宥庡幗閸嬪鏌ｅΟ娆惧殭缁炬儳娼￠弻鐔虹磼閵忕姵鐏堥梺鍛婂姀閸嬫捇姊绘担瑙勫仩闁稿寒鍣ｅ鏌ュ煛閸涱厾顦銈嗘煥閻ㄥ嘲危閸儲鐓欓柟顖嗗啯姣愬銈嗗竾閸ㄤ粙寮婚敐澶婄鐎规洖娲﹂幉濂告⒑閻熸澘妲婚柟铏耿閻涱喖顫滈埀顒勫箠閻樻椿鏁嗗璺猴攻閸婃盯姊婚崒姘偓鎼佸磹閹间礁纾归柟闂寸贰閺佸銇勯幘璺轰汗闁哄妫冮弻鐔告綇妤ｅ啯顎嶉梺鎶芥敱鐢偤鎯€椤忓牆绠氱憸婊堝磿閹达附鐓熸繛鎴炲笚濞呭﹪鏌?
     */
    private int externalLabelGap = 4;
    /**
     * 闂傚倸鍊风粈浣圭珶婵犲洤纾婚柛鏇ㄥ€犲☉妯滄棃宕熼埡鈧Ч妤呮⒑閸濆嫬鏆欓柣妤€妫濆畷姗€鍩€椤掑嫭鈷戦柟鑲╁仜閸旀鏌￠崨顏呮珚鐎规洘绻堥、姘跺焵椤掑嫬钃熸繛鎴炵矌閻も偓闁瑰吋鐣崹濠氬Χ閺夋娓婚柕鍫濇閻撱儳绱掔紒妯虹婵″弶鍔欓獮鎺楀箠瀹曞洤鏋涚€规洦鍋婂畷鐔碱敇閻愯尙鐣遍梻鍌氬€风欢姘焽閼姐倖瀚婚柣鏃傚帶缁€澶屸偓骞垮劚椤︿即宕戦崒鐐村€垫繛鎴烆伆閹寸偛鍨旈柟缁㈠枟閻撴瑩鏌熼鍡楀暞濮ｆ劙姊烘潪鎵槮闁绘牜鍘ч～蹇撁洪宥嗘櫓闂佸憡绻傜€氼剟鈥栫€ｎ亖鏀介柣鎰皺婢ф盯鏌涢妸銉т虎妞ゆ洩缍侀幃鐣岀矙鐠恒劌濮︽俊鐐€栫敮鎺楀磹閻戣姤鍊垮┑鐘叉川閸欐捇鏌涢妷锝呭闁靛棙甯為幉鎼佹焽閿曗偓閻忥附顨ラ悙瀵稿婵炵厧绻樺畷婊嗩槾闁哄棎鍊曢埞鎴﹀焺閸愩劎绁峰┑鐐叉▕閸樺ジ鎮鹃悜钘夌疀妞ゆ柨澧介悾鍫曟煟閻愬鈻撻柍褜鍓欓崢鏍ㄧ珶?
     */
    private int minInsideLabelHeight = 18;
    /**
     * 闂傚倸鍊风粈浣圭珶婵犲洤纾婚柛鏇ㄥ€犲☉妯滄棃宕熼埡鈧Ч妤呮⒑閸濆嫬鏆欓柣妤€妫濆畷姗€鍩€椤掑嫭鈷戦柟鑲╁仜閸旀鏌￠崨顏呮珚鐎规洘绻堥、姘跺焵椤掑嫬绠栨慨妞诲亾闁诡喗鐟ч埀顒佺⊕椤洭藝椤撱垺鈷戦梻鍫熺⊕婢跺嫰鏌ょ憴鍕闁诲繑甯″娲倷閽樺濮庣紒鍓ц檸閸樻儳鈽夐悽绋跨劦妞ゆ帒瀚埛鎴︽煙缁嬫寧鎹ｉ柍顖涙礋閺屻倛銇愰幒鏃傛毇闂佺硶鏂侀崑鎾愁渻閵堝棗绗掗柛瀣瀹曟洟鎮╃紒妯煎帗閻熸粍绮撳畷妤€鈽夐姀鈥虫畱闁荤娀缂氶妴鈧柡瀣Ч閺屾洘绻涢悙顒佺彅缂備讲鍋撻柛鈩冪⊕閻撴洟鏌嶉埡浣告灓闁绘帟濮ゆ穱濠囨嚑椤掆偓閸樻挳鏌＄仦璇插闁宠棄顦灒缂備焦蓱鐎氭娊姊绘担瑙勫仩闁告柨锕畷婵嬪箣濠靛牊娈鹃梺瑙勫婢ф宕戦崒鐐茬缂侇喛顫夐～濠冪箾婵傞晲鎲炬慨濠呮閹奉偅鎯旈垾鎰佷患闂佸憡鏌ｉ崐婵嬪蓟濞戙垹惟闁靛鏅╅埀顒侇殘閹喖鈻庨幘瀵稿幐闂佺鏈划宀勫礉閿曞倹鐓?
     */
    private StackLabelMode stackLabelMode = StackLabelMode.VALUE_PERCENT;
    /**
     * 闂傚倸鍊风粈浣圭珶婵犲洤纾婚柛鏇ㄥ€犲☉妯滄棃宕熼埡鈧Ч妤呮⒑閸濆嫬鈧摜鑺遍柆宥呭嚑闁哄啫鐗婇埛鎺楁煕椤愩倕鏋旈柍绗哄€曢湁婵犲ň鍋撻柛妤佸▕楠炲啫螣鐠恒劎鏉搁梺瑙勫劤婢у酣顢欐径濞炬斀闁挎稑瀚禒婊堟煕婵犲倹鍋ョ€殿噮鍋勯濂稿炊閿旇棄濯伴梻浣虹帛閸旓箓宕曢悜鑺ユ櫇闁稿本绋撻崢鍗炩攽椤旂煫顏呮櫠娴犲鍋╅梺顒€绉甸悡鐔兼煏婵犲繒鍒伴柣蹇氬皺閳ь剚顔栭崰鏇犲垝濞嗗繒鏆﹂柛顐ｆ礀閻撴稑鈹戦悩鎻掆偓姝屸叿缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啯绻濋崒婊勬濠殿喗顭堥崺鏍磻鐎ｎ喗鐓曟い鎰Т閻忊晝鎲搁幍顔煎妺缂佺粯绋掑蹇涘礈瑜忚摫闂備胶鎳撻幉锟犲箖閸屾繃顥ゅ┑鐘垫暩婵數鍠婂澶嬪€垮Δ锝呭暞閻撳繐鈹戦悩鑼鐎光偓濞戙垺鐓?
     */
    private boolean showSmallStackLabelOutside = true;
    /**
     * 缂傚倸鍊搁崐椋庢閿熺姴纾诲璺猴功閺嗭箓姊婚崼鐔烩偓钘壝洪鍛罕闂佸壊鍋€閹冲洭寮稿▎鎾寸厽閹兼惌鍨崇粔鐢告煕閻樻剚娈滈柡浣哥Ч閹垽宕楅懖鈺佸箺闂備線娼ч…鍫ュ磿闁稁鏁傛い鎾跺枍缁诲棙銇勯幇鈺佺仾闁稿鍎查幈銊︾節閸モ晝鏆悗娈垮櫘閸ｏ綁宕洪埀顒併亜閹烘垵顏柛搴★攻閵囧嫰寮崒娑欑彧闂佽崵鍠庣紞濠囧箖濡ゅ懏鏅查柛娑卞弾閺嗐垻绱撴担鎻掍壕婵犮垼鍩栭崝鏍偂閺囥垺鐓冮柛婵嗗椤忣亪鏌曢崱妤€鏆熺紒杈ㄥ浮閳ワ箓骞嬪┑鍡忔嫬闁诲氦顫夊ú鈺冩崲濠靛棭鍤曢柡澶嬪焾濞尖晠鎮归搹鐟板妺闁汇倕鍊荤槐鎾诲磼濞嗘垼绐楅梺绋款儏椤︻垶鎮鹃悜鑺ヮ棃婵炴番鍨哄浠嬬嵁瀹ュ鏁婇柣鐔碱暒婢规洜绱掔紒銏犲箹闁瑰啿绻樿棢濠㈣埖鍔栭崐鐢告煟閻旂顥嬮柨娑樼Ч閺岀喎鐣￠柇锕€鍓堕梺杞扮缁夌懓鐣烽悢纰辨晣闁绘柨鎽滃Σ妤呮⒒?
     */
    private double categoryGapRatio = 0.24D;
    /**
     * 闂傚倸鍊风粈渚€骞夐敓鐘冲殞闁绘劦鍓涢梽鍕煛閸愩劌鈧宕″鑸靛€甸梻鍫熺⊕閹叉悂鎮樿箛鏇熸毈闁哄被鍊栭幈銊╁箛椤戣棄浜鹃柡宥庡亞閻滅粯淇婇妶鍌氫壕闂佸疇妫勯ˇ鐢哥嵁濮椻偓楠炲洦鎷呯粙鍨棊闂傚倷鑳剁划顖炲箰閹间緡鏁嬫い鎾卞灩閻掑灚銇勯幒宥堝厡闁愁垱娲熼弻娑㈠籍閳ь剛鏁悙闈涘灊濠电姵纰嶉弲鎻掝熆鐠轰警鍎岄柟宄邦煼閺岋絾鎯旈妸锔介敪闂佺顕滅换婵嬪箖妤ｅ啯鐒肩€广儱妫岄幏濠氭⒑缁嬫寧婀扮紒瀣笧娴滄悂鏁傞悾宀€顔曢梺鍓插亗缁€渚€宕銏犵婵°倐鍋撴俊顐ｏ耿閺屾盯骞樺Δ鈧崐濠氭儗濡ゅ懏鈷掑ù锝囨嚀椤曟粎绱掔€ｎ偄鐏撮柟宕囧枛椤㈡盯鎮欓弶鎴滅盎闂備線鈧偛鑻晶顕€妫佹径瀣瘈濠电姴鍊搁弳锝嗐亜鎼淬埄娈橀柍褜鍓濋～澶娒哄鍫濈獥闁哄诞灞芥闂佷紮绲介張顒勫窗閸℃稒鐓曢柡鍥ュ妼楠炴牜鎲搁悧鍫熴仢闁哄矉绲鹃幆鏃堟晲閸℃鐣梻渚€鈧偛鑻晶浼存煕閿濆啫鍔︾€规洏鍎甸崺鈧い鎺戝€荤壕钘壝归敐鍛儓閺嶏繝姊洪棃娑欏缂侇喗鎹囧畷娲焵?
     */
    private double barGapRatio = 0.18D;
    /**
     * 闂傚倸鍊风粈浣虹礊婵犲偆鐒界憸鏃堛€侀弽顓炲窛妞ゆ棁妫勫鍧楁⒑閸愬弶鎯堥柟鍐叉捣缁粯瀵肩€涙鍘遍梺闈涱槹閸ㄧ敻骞婅箛鎾旀帗鎯旈妸锔规嫼濠殿喚鎳撳ú銈嗕繆婵傚憡鍊垫慨妯煎帶楠炴牠鏌曢崱妯烘诞鐎规洘绮嶉幏鍛槹鎼粹€斥挄闂傚倸顭崑鍕洪敃鍌氱闁圭虎鍠栫壕濠氭煙閹规劕鐓愰柛蹇旂矊椤啰鈧綆浜濋幑锝嗐亜閿旇鏋熺紒?null 闂傚倸鍊风粈渚€骞栭锕€鐤い鏍仜绾惧灝鈹戦崒婊庣劸闁告俺顫夌换婵囩節閸屾粌顣虹紓浣插亾濠㈣埖鍔栭悡蹇撯攽閻樿尙绠抽柣锝変憾閺屻倝妫冨☉姘毙ㄩ梺璇″枟椤ㄥ﹪骞冮挊澶嗘灁闁割煈鍠栨俊鎾⒒?
     */
    private Double minValue;
    /**
     * 闂傚倸鍊风粈浣虹礊婵犲偆鐒界憸鏃堛€侀弽顓炲窛妞ゆ棁妫勫鍧楁⒑閸愬弶鎯堥柟鍐叉捣缁粯瀵肩€涙鍘遍梺闈涱槹閸ㄧ敻骞婅箛鎾旀帗鎯旈妸锔规嫼濠殿喚鎳撳ú銈嗕繆婵傚憡鍊垫慨妯煎帶楠炴牠鏌曢崱妯烘诞闁硅櫕绮撳Λ鍐ㄢ槈濮樿京宓侀梺鑽ゅ枑缁瞼绮旇ぐ鎺戞槬闁逞屽墯閵囧嫰骞掗幋婵冨亾閸洖鐒垫い鎺嗗亾妞ゆ垵娲ゅ嵄闁归偊鍏橀弨浠嬫煕閵夈垺鏉归柡?null 闂傚倸鍊风粈渚€骞栭锕€鐤い鏍仜绾惧灝鈹戦崒婊庣劸闁告俺顫夌换婵囩節閸屾粌顣虹紓浣插亾濠㈣埖鍔栭悡蹇撯攽閻樿尙绠抽柣锝変憾閺屻倝妫冨☉姘毙ㄩ梺璇″枟椤ㄥ﹪骞冮挊澶嗘灁闁割煈鍠栨俊鎾⒒?
     */
    private Double maxValue;

    public BarChartElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public BarChartElement setTitle(String title) {
        this.title = title;
        return this;
    }

    public BarChartElement setPadding(Insets padding) {
        this.padding = padding;
        return this;
    }

    public BarChartElement setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public BarChartElement setAxisColor(Color axisColor) {
        this.axisColor = axisColor;
        return this;
    }

    public BarChartElement setGridColor(Color gridColor) {
        this.gridColor = gridColor;
        return this;
    }

    public BarChartElement setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    public BarChartElement setValueLabelColor(Color valueLabelColor) {
        this.valueLabelColor = valueLabelColor;
        return this;
    }

    public BarChartElement setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
        return this;
    }

    public BarChartElement setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        return this;
    }

    public BarChartElement setShowValueLabel(boolean showValueLabel) {
        this.showValueLabel = showValueLabel;
        return this;
    }

    public BarChartElement setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        return this;
    }

    public BarChartElement setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洨鐭嗗ù锝堫潐濞呯姴霉閻樺樊鍎愰柛瀣典邯閺屾盯鍩勯崘顏佹闂佸憡鐟ラ敃銉╁Φ閸曨垰鍐€闁靛ě灞炬闂備胶顭堟鎼佹晝閵堝鐓″鑸靛姇缁犲鎮归崫鍕儓濠碘€虫惈閳规垿鎮欓崣澶婃闂佹寧娲忛崹钘夘嚕婵犳艾宸濇い鎾楀嫬鍏婃俊鐐€栫敮鎺楀磹閹间焦鍊舵い鏇楀亾婵﹥妞藉畷銊︾節閸曨剙娅樺┑鐘愁問閸犳岸寮繝姘畺闁跨喓濮寸粻缁樸亜閺冨洦顥夊ù婊冪秺閺岋綁鎮╅悜姗嗕哗闂佺绨洪崐妤冨垝鐎ｎ喖閱囬柕澶涘閸?
     * 闂備浇顕х€涒晠顢欓弽顓炵獥闁圭儤顨呯壕濠氭煙閸撗呭笡闁绘挻娲橀幈銊ノ熼悡搴′粯闂佽绻掓繛鈧柡宀€鍠栭、娆戠驳鐎ｎ偆鏆﹂梺璇叉捣閺佹悂宕㈣閿濈偛鈹戠€ｅ灚鏅㈤梺閫炲苯澧查柕鍥ㄥ姍閹晝绱掑Ο閿嬪闂備胶顭堢悮顐﹀磹閺囥垻宓侀柡宥庡幗閻撶喖鏌″鍐ㄥ姎闁诲繗灏欓埀顒冾潐濞测晝绱炴笟鈧獮鍐煛閸涱厽顥濋梺闈涱槶閸庣増绔熼弴銏♀拺闁告繂瀚鈺傜箾鐎涙ê鍝虹€规洏鍎甸崺鈧い鎺戝閳锋垿鏌涘┑鍡楊伀鐎涙繂鈹戦悙鑼勾闁告梹鍨块妴浣肝旈崨顔尖偓濠氭煠閹帒鍔滄繛鍛墵閹宕楁径濠佸闂備礁鎲″ú锔界閻愬灚顫曟慨妯垮煐閳锋垹绱掔€ｎ厽纭剁紒鐘冲缁辨帗寰勬繝鍕剁礊缂備緡鍣崢濂告偩濠靛绀嬫い鎰剁畱閺佸綊姊绘担铏广€婇柛鎾寸箞閵嗗啴宕ㄩ婊咁槸閻庡厜鍋撻柛鏇ㄥ墰閸橀亶姊鸿ぐ鎺戜喊闁告挻宀稿畷鐢稿炊椤掍胶鍘遍柟鍏肩暘閸ㄨ櫣浜搁敂閿亾濞堝灝鏋欑紒顔界懃閻ｇ兘宕奸弴鐐靛幐婵犵數濮撮崐姝屸叿闂傚倸鍊风粈渚€骞夐敓鐘茬婵☆垶妫块悞濠冪箾閸℃ɑ灏伴柛瀣埞鎴︽偐閸欏鎮欓弶?
     */
    public BarChartElement setStacked(boolean stacked) {
        this.stacked = stacked;
        if (stacked) {
            this.showStackTotalLabel = false;
        }
        return this;
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洨鐭嗗ù锝堫潐濞呯姴霉閻樺樊鍎愰柛瀣典邯閺屾盯鍩勯崘顏佹闂佸憡鐟ラ敃銉╁Φ閸曨垰鍐€闁靛ě灞炬闂備胶顭堟鎼佹晝閵堝鐓″鑸靛姇缁犲鎮峰▎蹇擃伌濞寸姭鏅涢埞鎴︽倷閸欏娅￠梺鍛婃⒐閸ㄧ敻鎮惧畡閭︽僵閻犲搫鎼懓鍨攽閳藉棗鐏犻柛姘儏閺嗏晠姊婚崒娆戭槮濠㈢懓锕畷鎴﹀礋椤愬绋戣灃闁告洍鏅欏Ч妤呮⒑閸濆嫬鏆欓柣妤€妫濋幏鎴︽偄閻撳海楠囬梺鍓插亝绾板秴鈻嶉崨顖滅當闁硅揪闄勯埛鎴︽煙閼测晛浠滈柛鏃€鐟╅弻娑㈠即閻曞倽鈧潡鏌涢埞鎯т壕?
     * 闂備浇顕х€涒晠顢欓弽顓炵獥闁圭儤顨呯壕濠氭煙閸撗呭笡闁绘挻娲橀幈銊ノ熼悡搴′粯闂佽绻掓繛鈧柡宀€鍠栭、娆戠驳鐎ｎ偆鏆﹂梺璇叉捣閺佹悂宕㈣閿濈偛鈹戠€ｎ亞顦ㄩ梺鍛婄懃椤︿粙宕崼鏇熲拻濞达絽鎲￠崯鐐烘煛瀹€瀣М鐎规洘绻傞悾婵嬪礋椤愩倗鏆伴梺鐟板悑閻ｎ亪宕濆澶婄；闁靛ň鏅滈悡蹇撯攽閻愯尙浠㈤柛鏂诲€濋弻娑㈠Ω閵夘喚鍚嬮梺鍝勬湰缁嬫垿鍩㈡惔銊ョ婵犮埄鍓ㄧ紞渚€骞婂璺虹厸闁告劧绲芥禍鐐箾閸繄浠㈤柡瀣☉铻栭柣妯垮皺缁犺櫕淇婇崣澶婂闁宠鍨归埀顒婄秵閸撴瑧绮婇敃鍌涚厽闁绘ê寮堕幆鍫ユ煙绾板崬浜柕鍥ㄥ姍閹晝绱掑Ο閿嬪闂備胶顭堢悮顐﹀磹閺囥垻宓侀柡宥庡幗閻撶喖鏌″鍐ㄥ姎闁诲繗灏欓埀顒冾潐濞测晝绱炴笟鈧獮鍐煛閸涱厽顥濋梺闈涱槶閸庣増绔熼弴銏♀拺闁告繂瀚鈺傜箾鐎涙ê鍝虹€规洏鍎甸崺鈧い鎺戝閳锋垿鏌涘┑鍡楊伀鐎涙繂鈹戦悙鑼勾闁告梹鍨块妴浣肝旈崨顔尖偓濠氭煢濡尨绱?
     */
    public BarChartElement setPercentStacked(boolean percentStacked) {
        this.percentStacked = percentStacked;
        if (percentStacked) {
            this.stacked = true;
            this.showStackTotalLabel = false;
        }
        return this;
    }

    public BarChartElement setShowStackTotalLabel(boolean showStackTotalLabel) {
        this.showStackTotalLabel = showStackTotalLabel;
        return this;
    }

    public BarChartElement setYAxisTickCount(int yAxisTickCount) {
        if (yAxisTickCount < 2) {
            throw new PosterException("yAxisTickCount must be greater than or equal to 2");
        }
        this.yAxisTickCount = yAxisTickCount;
        return this;
    }

    public BarChartElement setAxisStrokeWidth(int axisStrokeWidth) {
        this.axisStrokeWidth = Math.max(1, axisStrokeWidth);
        return this;
    }

    public BarChartElement setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
        return this;
    }

    public BarChartElement setLabelFontSize(int labelFontSize) {
        this.labelFontSize = labelFontSize;
        return this;
    }

    public BarChartElement setLegendFontSize(int legendFontSize) {
        this.legendFontSize = legendFontSize;
        return this;
    }

    public BarChartElement setValueLabelFontSize(int valueLabelFontSize) {
        this.valueLabelFontSize = valueLabelFontSize;
        return this;
    }

    public BarChartElement setLegendItemGap(int legendItemGap) {
        this.legendItemGap = legendItemGap;
        return this;
    }

    public BarChartElement setLegendMarkerSize(int legendMarkerSize) {
        this.legendMarkerSize = legendMarkerSize;
        return this;
    }

    public BarChartElement setMaxBarWidth(int maxBarWidth) {
        this.maxBarWidth = maxBarWidth;
        return this;
    }

    public BarChartElement setMinBarWidth(int minBarWidth) {
        this.minBarWidth = minBarWidth;
        return this;
    }

    public BarChartElement setBarArc(int barArc) {
        this.barArc = Math.max(0, barArc);
        return this;
    }

    public BarChartElement setStackTotalLabelGap(int stackTotalLabelGap) {
        this.stackTotalLabelGap = Math.max(0, stackTotalLabelGap);
        return this;
    }

    public BarChartElement setExternalLabelGap(int externalLabelGap) {
        this.externalLabelGap = Math.max(0, externalLabelGap);
        return this;
    }

    public BarChartElement setMinInsideLabelHeight(int minInsideLabelHeight) {
        this.minInsideLabelHeight = Math.max(8, minInsideLabelHeight);
        return this;
    }

    public BarChartElement setStackLabelMode(StackLabelMode stackLabelMode) {
        this.stackLabelMode = Optional.ofNullable(stackLabelMode).orElse(StackLabelMode.VALUE_PERCENT);
        return this;
    }

    public BarChartElement setShowSmallStackLabelOutside(boolean showSmallStackLabelOutside) {
        this.showSmallStackLabelOutside = showSmallStackLabelOutside;
        return this;
    }

    public BarChartElement setCategoryGapRatio(double categoryGapRatio) {
        this.categoryGapRatio = categoryGapRatio;
        return this;
    }

    public BarChartElement setBarGapRatio(double barGapRatio) {
        this.barGapRatio = barGapRatio;
        return this;
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洨鐭嗗ù锝堫潐濞呯姴霉閻樺樊鍎愰柛?Y 闂傚倷绀侀幖顐λ囬柆宥呯；婵炴垯鍨洪崑銈夋煏婵炲灝鍓婚柣鏃傤焾缁剁偟鈧厜鍋撻柍褜鍓熼幆宀勫幢濡炪垺妫冮弫鍐焵椤掑嫭鍊舵繝闈涱儐閸庡﹪姊洪鈧粔鐢告偂閺囥垺鐓忓鑸电☉椤╊剟鏌￠崱姗堣€块柡?
     * 濠电姷鏁搁崑娑㈩敋椤撶喐鍙忓Δ锝呭枤閺佸﹤鈹戦悩鎻掝伀闁告宀搁幃妤呮濞戞瑦鍠愰梺鍛婎殕绾板秹濡甸崟顖氬唨闁靛ě鍕珯缂傚倷璁查崑?null 闂傚倸鍊风粈渚€骞栭锕€鐤い鏍仜绾惧灝鈹戦崒婊庣劸鏉╂繈姊洪崨濠傚Е濞存粎鍋ら幆宀勫幢濞戞瑧鍘介梺鐟邦嚟閸庢垿鎳撻崸妤佺厱閹兼番鍨虹亸锕傛煛瀹€瀣М闁挎繄鍋ら、妤呭焵椤掍焦鍙忛柍褜鍓熷娲箹閻愭祴鍋撻弽顐㈠灊闁规儳纾弳锔界節闂堟侗鍎忛幆鐔奉渻閵堝骸骞楅柛銊ф暬楠炲啯鎷呴搹鍦紳婵炶揪绲介幖顐ｇ墡婵＄偑鍊戦崝宀勬偋韫囨稑绀嗛柟鐑橆殔鎯熼梺瀹犳〃缁€渚€宕甸幋婵冩斀闁绘ɑ鐟ラ崯鏉款潩閵娾晜鐓?
     */
    public BarChartElement setValueRange(Double minValue, Double maxValue) {
        if (minValue != null && maxValue != null && minValue >= maxValue) {
            throw new PosterException("minValue must be less than maxValue");
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        return this;
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洨鐭嗗ù锝堫潐濞呯姴霉閻樺樊鍎愰柛瀣典邯閺屾盯鍩勯崘顏佹缂備胶濮伴崕鐢稿蓟瀹ュ牜妾ㄩ梺鍛婃尵閸犲酣顢氶敐澶婂瀭妞ゆ劑鍨荤粣鐐烘倵閸忓浜鹃梺鍛婂姉閸嬫捇鎷烘径鎰拻濞达絽鎼崝锕傛煛閸涱亝娅婄€规洑鍗冲浠嬵敇閻愮數鏆梻浣告惈濞层劍鎱ㄩ悜鑺ュ剹闁瑰墽绮崑銊︺亜閺嶇數鍒伴柡瀣ㄥ€楃槐鎺楀焵椤掍礁绶炲┑鐐村笒缂嶅﹤鐣峰Δ鍛拻缂傚牏濮撮銏′繆閻愵亜鈧劙寮查埡鍛；濠电姴娲ょ粻姘舵煕閺囥劌鐏犻柛鎰ㄥ亾闁荤喐绮岀粔鐟邦嚕椤掑喚妯勯梺鍝勭焿缁绘繂鐣烽悡搴樻斀闁归偊鍘滈妸鈺傜厽闁绘劖娼欓崵顒勬煕鐎ｎ亷韬鐐叉瀹曠喖顢涢敐鍡樻珦闂備胶绮幐绋棵归柨瀣浄婵鍩栭悡鐔兼煟閹邦剦鍤熼柍绗哄€濋弻娑㈠棘閸柭ゅ惈闂佺硶鏂侀崑?
     */
    public BarChartElement setCategories(List<String> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }
        return this;
    }

    public BarChartElement addCategory(String category) {
        this.categories.add(category);
        return this;
    }

    /**
     * 婵犵數濮烽弫鎼佸磿閹寸姷绀婇柍褜鍓氶妵鍕即閸℃顏柛娆忕箻閺岋綁骞囬澶婃闂佸磭绮Λ鍐蓟瀹ュ牜妾ㄩ梺鍛婃尰濮樸劎鍒掑▎鎰窞闁规澘鐏氶弲婊堟⒑閸涘﹣绶遍柛鐘愁殜椤㈡瑥顓兼径瀣ф嫼缂佺虎鍘奸幏瀣吹閸愵喗鐓曢柣妯哄暱閸濇椽鏌＄仦鐣屽ⅵ妤犵偞锕㈤、娆撴嚃閳哄﹥孝濠电姷顣槐鏇㈠磻濞戙垺鍎嶉柣鎴ｆ绾?
     */
    public BarChartElement addSeries(BarChartSeries series) {
        if (series == null) {
            return this;
        }
        this.seriesList.add(series);
        return this;
    }

    public BarChartElement addSeries(String name, List<? extends Number> values) {
        return addSeries(BarChartSeries.of(name, values));
    }

    public BarChartElement addSeries(String name, List<? extends Number> values, Color color) {
        return addSeries(BarChartSeries.of(name, values).setColor(color));
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        // 闂傚倸鍊烽懗鍫曗€﹂崼銏″床闁规壆澧楅崑瀣攽閻樺弶澶勯柛瀣儔楠炴牕菐椤掆偓閻掔儤绻涢崼銏℃珪闁逞屽墮閸樻粓宕戦幘缁樼厱闁规澘鍚€缁ㄤ粙鏌ｉ幇顓熺婵﹨娅ｇ划娆戞崉閵娧呮澖闂備胶顭堢€涒晠鎮￠垾鎰佸殨闁规儼濮ら崑鍕煟閹捐櫕鎹ｆい鏂款槸椤啴濡堕崱妯碱槬濠碘槅鍋呯粙鎾跺垝閸喎绶為柟閭﹀幐閹峰搫顪冮妶鍡楀潑闁稿鎹囬弻锝堢疀閺冨倻鐤勯悗瑙勬磸閸ㄨ姤淇婇懜闈涚窞濠电姴鍟粊鍫曟⒒娓氣偓濞佳嗗櫣闂佸憡渚楅崢楣冾敃婵傚憡鈷掑ù锝呮啞閸熺偤鎮介娑樻诞闁诡喓鍎甸幃锟犵嵁椤掑倸鏋戦柟宄版噽閸栨牠寮撮悢杞扮按闂傚倸鍊搁崐鎼佹偋婵犲嫮鐭欓柟瀛樼渤閹烘挸绶為柟閭﹀幘閸樿棄鈹戦悩璇у伐闁瑰啿閰ｉ妴鍌涚附閸涘﹦鍘搁梺绯曞墲閻熴儵寮搁弮鍌滅＜閺夊牄鍔屽ù顔锯偓瑙勬礈閸犳牠銆佸▎鎾崇婵☆垳顭堟慨鍌炴煛瀹€瀣М闁轰焦鍔欏畷銊╊敊閼恒儱顏伴梻鍌欑劍閹爼宕濇繝鍥х闁瑰瓨绻嶉崵鏇熴亜閺囨浜鹃梺璇″枓閺呯娀骞栬ぐ鎺濇晝闁挎繂娲ょ紞鎴︽⒒娴ｇ瓔鍤欑紒缁樺姉閹广垽骞囬弶璺啇濡炪倖鍔х€靛矂寮€ｎ偁浜滈柟鎯у船閻忊晜娼?
        validateData();
        Graphics2D graphics = context.getGraphics();
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            Point origin = dimension.getPoint();
            // 闂傚倸鍊风粈渚€骞栭锔藉亱闁糕剝铔嬮崶顒€绠柦妯侯槺閿涚喎鈹戞幊閸婃洟骞婃惔銊﹀仾闁绘劦鍏欐禍婊堟煛瀹ュ啫濡跨紒鐘虫尰閵囧嫰濡烽妷褏顔掗梺鍝勭焿缁绘繂鐣峰鈧、妯款槻闁哄拑绲跨槐鎾存媴娴犲鎽电紓浣筋嚙閻楀棝鎮惧畡鎷旀棃宕ㄩ銏犳暪闂備線娼х换鍫ュ垂閸︻厾绠旈柨鐔哄У閳锋垿鏌涘☉姗堝伐缂佹せ鏅滅换娑欏緞鐎ｎ兘妲堥梻鍥ь槹娣囧﹪顢涘顒佸€梺绋款儐閿曘垽寮婚弴鐔虹闁割煈鍠栭‖鍫ユ⒑閸涘﹥灏紓宥咃躬瀵鏁撻悩鍙夈仢婵炶揪绲介幗婊勭閳哄懏鐓熼煫鍥ㄦ尰缁傚鏌涢妸銊︾【闁伙絽鍢茬叅妞ゅ繐鎳庢禍婊呯磽娓氬洤鏋ら柟铏尰缁?
            if (backgroundColor != null) {
                g.setColor(backgroundColor);
                g.fillRect(origin.getX(), origin.getY(), width, height);
            }

            // 濠电姷鏁搁崑鐐差焽濞嗘挸瑙﹂悗锝庡枟閺咁亪姊绘担鍛婂暈閽冭京绱掔€ｎ偄娴鐐茬箻瀹曘劑顢涘☉妯硷紡闂備線娼ч…顓㈡⒔閸曨偀鏋旈柕蹇ョ磿缁犻箖鏌熺€电浠﹂悘蹇ｅ弮閺屻劑寮撮鍡樺仹缂備緡鍣崣鍐ㄧ暦閵娾晛绾ч柟瀵稿剱濡喖姊绘担瑙勫仩闁稿氦宕电划濠氬箳濡も偓閸屻劍銇勯幒鎴濃偓鐢稿磻閹捐鎹舵い鎾楀懎濮奸柣搴㈩問閸犳牠鎮ユ總绋跨畺婵°倕鎷嬮弫宥夋煟閹邦剦鍤熼柛鏃傛暬濮婅櫣绱掑Ο蹇ｄ邯閹洦瀵奸弶鎴狅紱闂侀潧艌閺呮粓鎮￠悢鍏肩厪濠电偛鐏濋崝姘亜韫囧﹥娅婇柡宀嬬秮閸╁﹤鈻庤箛鎿冧痪婵犳鍨遍幐鎶藉蓟閻旂厧鍨傛い鏃傗拡娴尖偓缂傚倷璁查崑鎾愁熆閼搁潧濮堥柣鎾存礃閹便劌螣閸濆嫧鎸冮梺浼欑秮娴滃爼寮诲☉鈶┾偓锕傚箣濠靛牅妗撴俊銈囧Х閸嬬偤宕濋弽褜鍤楅柛鏇ㄥ灠缁犳稒銇勯幒鍡椾壕闁荤姍鍐惧剳缂佽鲸鎹囧畷鎺戭潩椤掍胶浜鹃梻浣告憸婵敻鎮уΔ鍛疄闁靛ň鏅涢悡娑㈡煕濞戝崬骞橀柣锝堥哺缁绘稒娼忛崜褎鍋у銈庡幖閻楁捇骞冨鈧弫宥夊礋閵娿儰澹曢梺缁樺姉閹虫捇鍩€椤掍胶澧い顏勫暣閹墽浠﹂挊澶屼喊婵＄偑鍊栭悧妤冨垝瀹ュ棗鍨斿┑鍌氭啞閻撳繘鏌涢锝囩畺闁革絼绮欓弻鐔兼偩鐏炶姤鐎婚梻鍥ь樀閺屻劌鈹戦崱姗嗘！闁诲繐娴氭禍顏堝蓟?
            Font baseFont = Optional.ofNullable(context.getConfig().getFont()).orElse(
                    new Font(context.getConfig().getFontName(), context.getConfig().getFontStyle(), context.getConfig().getFontSize())
            );

            // 闂傚倸鍊烽懗鍫曗€﹂崼銏″床闁规壆澧楅崑瀣煙娴兼潙浜伴柡浣告喘閺屾稑鈹戦崟顐㈠Б婵炴垶鎸哥粔鍨┍婵犲洤围闁告洦鍘兼俊钘夆攽閻愯尙澧曢柣鏍с偢瀵鈽夊▎鎴犵槇闂佸憡娲﹂崑鍕濮椻偓閺岋綀绠涢幘瀵割洶闂佹悶鍔忓▔娑綖韫囨洜纾兼俊顖濆吹缁夊爼姊洪崨濠冨瘷闁告粈鐒﹀暩闂傚倸鍊风粈渚€骞夐敓鐘茬闁哄洨濮烽惌鎾绘⒒閸喕鍎愰幖娣妼缁狀噣鏌ら幁鎺戝姎濞寸姵妞藉铏圭磼濡崵鍙嗘繝鈷€鍐弰闁诡喖鍢查埞鎴犫偓锝庡亞閸樺崬鈹戦鏂や緵闁告挻鐩幃姗€鏌嗗鍡欏幐婵炶揪缍佸褔鍩€椤掍胶绠炵€殿喖顭峰畷銊╁级閹寸姷娼夐梻浣侯焾閺堫剛绮欓幘璇叉辈闁绘绮悡鐔煎箹濞ｎ剙鐏柍顖涙礋閺屾洟宕堕埡浣锋埛缂備浇浜崑鐔煎焵椤掑﹦绉甸柛鐘崇墳缁?
            ChartValueRange valueRange = resolveValueRange();
            Font titleFont = baseFont.deriveFont(Font.BOLD, (float) titleFontSize);
            Font labelFont = baseFont.deriveFont(Font.PLAIN, (float) labelFontSize);
            Font legendFont = baseFont.deriveFont(Font.PLAIN, (float) legendFontSize);
            Font valueFont = baseFont.deriveFont(Font.PLAIN, (float) valueLabelFontSize);

            // 闂傚倸鍊烽懗鍫曗€﹂崼銏″床闁规壆澧楅崑瀣攽閻樻彃鏆炴繛鍛█閺岀喖姊荤€电濡介梺鎶芥敱濡啴寮婚弴銏犻唶婵犻潧娲ゅ▍锝夋⒑鏉炴壆顦﹂柣鏍帶椤曪綁宕奸弴鐐哄敹闂侀潧绻嗛弲婊堝极椤栨粎纾藉ù锝囨嚀婵牓鏌ｉ埡濠傜仸鐎殿噮鍋勯濂稿炊閿旇棄濯伴梻浣风串缁蹭粙鎯夐懖鈺冧笉闁圭儤顨嗛埛鎴︽煕濞戞﹫宸ョ紒妤佺缁绘繈濮€閳浜﹢浣糕攽閳藉棗鐏犻柛姘儔瀹曟垵顫滈埀顒勫蓟閺囩喓绠鹃柛顭戝枛婵酣姊烘潪鎵槮闁绘牜鍘ч～蹇撁洪宥嗘櫓闂佸憡绻傜€氼剟鈥栭崼銉﹀仭婵犲﹤鎳庨。濂告煟閹垮嫮绡€妤犵偛鍟粋鎺斺偓锝庘偓顓涙櫊閺屾洘寰勯崼婵嗗Е濠电偛鐗滈崜鐔奉潖閾忕懓瀵查柡鍥╁枑閻濇牠姊洪崫銉バ㈡俊顐ｇ箞瀹曟椽鍩€?
            int innerLeft = origin.getX() + padding.left;
            int innerTop = origin.getY() + padding.top;
            int innerRight = origin.getX() + width - padding.right;
            int innerBottom = origin.getY() + height - padding.bottom;

            // 闂傚倸鍊风粈渚€骞栭銈囩煋闁哄鍤氬ú顏嶆晣妞ゎ偒鍏橀崑鎾诲磼濞戞凹娴勯柣搴秵娴滄繈濡靛┑瀣厽闊洦娲栨禒鈺佲攽椤斿搫鈧鍒掔€ｎ喖閱囨繝闈涘閸嬫捇骞掗幋顓熷兊濡炪倖甯掗ˇ顖炲礈椤斿墽纾藉ù锝囶焾缂嶄線鏌涘Δ浣糕枙鐎殿喖顭峰畷銊╁级閹寸姷鏉告俊鐐€栭幐鍡涘礃閵婂函绻濆缁樻媴閸涘﹤鏆堝銈冨妼閻楀﹪骞堥妸鈺傚€婚柦妯侯槹閻庮剟姊洪棃娑氬婵☆偄瀛╃粋宥咁煥閸喓鍘卞銈嗗姧缁插墽绮堥埀顒勬⒑閸涘﹥鐓熼柛搴＄－濡叉劙骞掗弬鐐媰闂佺粯鍔﹂崗娆愮閵忕姭鏀介柍钘夋娴滄粍绻涚亸鏍у⒉濞?
            int titleHeight = drawTitle(g, innerLeft, innerTop, innerRight, titleFont);
            innerTop += titleHeight;

            int legendHeight = drawLegend(g, innerLeft, innerTop, innerRight, legendFont);
            innerTop += legendHeight;

            // 闂傚倸鍊风粈渚€骞栭銈囩煋闁绘垶鏋荤紞鏍ь熆鐠虹尨鍔熼柡鍡愬€曢妴鎺戭潩閿濆懍澹曢柣搴ゎ潐濞叉ê顫濋妸鈺佺闁绘顕х粻鐢告煙閻戞绠撻柡浣风矙濮婄粯鎷呴崨濠冨創闂佺锕﹂…鍫㈠弲闂佹寧娲嶉崑鎾绘煥閺囨ê鐏叉鐐茬Ч椤㈡瑩鎮锋０浣割棜缂備胶铏庨崢濂稿箠韫囨蛋澶嬪緞閹邦厼鈧數鐥鐐村櫤闁绘繍浜弻銊モ槈濞嗗繐顫掗悗瑙勬礈閸犳牠銆佸鈧幃銏ゅ传閵夛附顔?Y 闂傚倷绀侀幖顐λ囬柆宥呯；婵炴垯鍨洪崑銈夋煏婵炵偓娅嗛柛瀣樀閺屻劌鈹戦崱妯虹獩婵＄偠顫夋繛濠囧蓟閻旂⒈鏁嶉柛鈩冾殔閺嗭絾绻涢崼顐㈠⒋婵﹨娅ｉ崠鏍即閻斿摜褰囬梻浣告憸婵潙螞濠靛鏄ラ柍?
            List<Double> ticks = createTicks(valueRange);
            g.setFont(labelFont);
            FontMetrics labelMetrics = g.getFontMetrics();
            int yAxisLabelWidth = calcYAxisLabelWidth(labelMetrics, ticks);

            // X 闂傚倷绀侀幖顐λ囬柆宥呯；婵炴垯鍩勯弫瀣亜閹捐泛校闁稿鐗楅妵鍕箛閸洘顎嶉梺绋块鐎涒晠濡甸崟顖氱睄闁稿本绋掗悵顏堟⒑閹肩偛濮€闁稿鍔楀Σ鎰板箳閹存梹顫嶅┑鐐叉缁绘劖鎱ㄩ姀銈嗏拺缁绢厼鎳忛懙褰掓煕濡も偓閸熷潡顢氶敐澶樻晝闁挎洍鍋撶紒鐙欏洤绠归悗娑櫳戠亸顓灻瑰鍫㈢暫闁哄本绋栫粻娑橆潩椤戞寧鐫忛梻浣规偠閸斿繘宕戦幇顔筋潟闁圭儤鍨熷Σ鍫ユ煏韫囧鐏柡鍡╁亰濮婅櫣绮欑捄銊ь唹闂佹寧娲忛崹纭呯亱婵炴挻鍩冮崑鎾绘煕閳规儳浜炬俊鐐€栫敮鎺楀磹瑜版帒鐤柛婵嗗閳ь剚甯掗～婵嬫晲閸涱剙顥氬┑锛勫亼閸娧呭緤濞差亜鐤鹃柣妯款嚙缁犳牠鏌熼鍡忓亾闁哄閰ｉ悡顐﹀炊閵婏妇锛涙繛瀛樺殠閸ㄨ棄顫忕紒妯肩懝闁搞儜鍕簴闂備礁鎲￠弻銊╂煀閿濆鏄ラ柣鎰惈閸愨偓濡炪倖鎸鹃崯鍧楀箯鐠囨祴鏀介柣鎴濇川缁夌敻鏌涜箛鏃撹€跨€?
            int xAxisLabelAreaHeight = labelMetrics.getHeight() + 8;
            if (showValueLabel) {
                g.setFont(valueFont);
                xAxisLabelAreaHeight += g.getFontMetrics().getHeight() / 4;
            }
            int plotLeft = innerLeft + yAxisLabelWidth + 10;
            int plotTop = innerTop + 4;
            int plotRight = innerRight;
            int plotBottom = innerBottom - xAxisLabelAreaHeight;
            int plotWidth = plotRight - plotLeft;
            int plotHeight = plotBottom - plotTop;
            if (plotWidth <= 0 || plotHeight <= 0) {
                throw new PosterException("chart drawable area is too small");
            }

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 0 闂傚倸鍊风粈渚€骞夐敍鍕殰闁绘梻鈷堥弫瀣喐韫囨稑鐒垫い鎺戝枤濞兼劕鈹戦悙璇ц含闁绘侗鍠栭鍏煎緞婵犲嫬濮︽俊鐐€栫敮鎺斺偓姘煎墴楠炴垿宕奸弴鐔哄帾婵犵數鍋涢悘婵嬪焵椤掍緡娈樼紒顔芥⒒閳ь剟娼ч幗婊呭娴犲鐓曢柟閭﹀幖閸斻倝鏌＄€ｎ剙鏋戝ǎ鍥э躬椤㈡洟顢楅崒婊勬闁诲氦顫夊ú姗€鏁冮姀鐘垫殾婵せ鍋撻柟宕囧█椤㈡鎷呴崷顓熜?X 闂傚倷绀侀幖顐λ囬柆宥呯；婵炴垯鍩勯弫瀣亜閹捐泛孝闁搞劍绻冮妵鍕冀椤愵澀绮剁紓浣插亾闁糕剝绋掗悡娑㈡煕椤愮姴鐏柡鍡忔櫇缁辨帗寰勬繝搴℃缂備浇椴搁幑鍥х暦濮椻偓楠炲棜顦抽柣婵愬灣缁辨挻鎷呮禒瀣懙缂備浇顕ч崯鎵垝濞嗘劗绡€闁搞儯鍔屾禍婊堟倵閻熸澘顏褎顨婇幃妤咁敃閿旂晫鍘介柟鍏肩暘閸娿倕顭囬幇顔炬／闁哄娉曟晥濡炪們鍨洪〃鍛粹€﹂妸鈺佸窛妞ゆ梻鈷堟导鏇㈡⒒娴ｈ櫣甯涢柡灞诲姂楠炴顭ㄩ崼婵堢厬闂佸湱铏庨崰妤呭磹閸偆绠鹃柟瀵稿仧閹冲啫鈹戦纰辨Ч缂佺粯鐩畷濂告偄妞嬪簼绱濋梻浣筋嚃閸犳洜鍒掑▎鎾崇畺闁靛繈鍊曠粈鍌炴煕韫囨艾浜规俊宸邯濮婄粯鎷呴崨濠冨創闂佺锕ら…鐑界嵁婵犲洤绠婚悹鍥皺閻ｅ搫鈹戦悩璇у伐闁绘妫滅换?
            int zeroY = calculateZeroY(plotTop, plotBottom, plotHeight, valueRange);

            drawGridAndAxis(g, plotLeft, plotTop, plotRight, plotBottom, zeroY, ticks, valueRange, labelFont);
            drawBars(g, plotLeft, plotTop, plotRight, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
            drawXAxisLabels(g, plotLeft, plotBottom, plotWidth, labelFont);
        } finally {
            g.dispose();
        }
        return dimension.getPoint();
    }

    @Override
    public void beforeRender(PosterContext context) {
        super.beforeRender(context);
    }

    /**
     * 闂傚倸鍊风粈渚€骞栭銈囩煓闁告洦鍘藉畷鍙夌節闂堟侗鍎愰柛瀣戠换娑㈠幢濡纰嶉梺缁樺姇閿曨亪寮婚悢鐑樺枂闁告洦鍋勯～鍥⒑闁偛鑻晶顕€鏌涢悢鍝勵暭缂佸倸绉归獮鏍ㄦ媴闂€鎰倞闂備礁鎲″ú鐔奉焽瑜嶉埢鎾诲醇閺囩啿鎷洪梺鍛婄☉閿曪箓鎯屾繝鍥ㄧ厸闁割偒鍋勬晶瀵糕偓娈垮枟婵炲﹪骞冮姀銈嗗亗閹艰揪缍嗛崬瑙勪繆閻愵亜鈧牠寮婚妸锔芥珷闁芥ê顦弸鏍煏韫囧鈧牠鎮″☉銏″€甸柨婵嗙凹缁ㄧ粯銇勮箛锝勭凹濞ｅ洤锕畷锝嗗緞鐎ｎ亖鍋撻幇顔瑰亾鐟欏嫭纾搁柛搴ｆ暬瀵偊宕橀鑲╁姦濡炪倖甯掔€氼剛绮婚弽顓炵缂侇喛顫夐鍡涙煛鐎ｎ偆澧甸柡宀嬬節瀹曞爼濡搁崶銊ュ摵鐎?
     */
    private void validateData() {
        if (width <= 0 || height <= 0) {
            throw new PosterException("chart width and height must be greater than 0");
        }
        if (categories.isEmpty()) {
            throw new PosterException("chart categories can not be empty");
        }
        if (seriesList.isEmpty()) {
            throw new PosterException("chart series can not be empty");
        }
        for (BarChartSeries series : seriesList) {
            if (series.getValues().size() != categories.size()) {
                throw new PosterException("series value size must match category size");
            }
        }
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啯绻濋崒銈呮婵炲濮撮鍛存倷婵犲啨浜滈柟鐑樺焾濡茶鎱ㄩ敐鍛劯婵﹥妞介獮鎰償閿濆倹顫嶉梻浣稿悑濡炲潡宕归崼鏇炵疇闁绘柨鍚嬮崑鍕煕濞戞﹫姊楁慨姗堢畵濮婃椽宕崟顐熷亾閸︻厸鍋撶粭娑樻噽閻棝鏌嶈閸撶喎顫忓ú顏勪紶闁告洦鍘滈姀锛勭鐎瑰壊鍠栧顕€鏌曢崱妤€鈧潡骞冮崜褌娌紒瀣仒婢规洟姊洪棃鈺佺槣闁告ê鍚嬬粋鎺撴綇閵娿倗绠氱紓鍌欓檷閸ㄥ綊鎮橀敂閿亾鐟欏嫭绌跨紒缁樼箞楠炲啴鍩￠崨顓炵€銈嗗姧缁查箖顢樿ぐ鎺撯拻濞达絽婀卞﹢浠嬫煕閺傝法鐏遍柍褜鍓氶惌顕€宕￠幎钘夌畺闁汇垹澹婇弫濠囨煠閹帒鍔氶柛鏂挎嚇濮婃椽骞愭惔銏╂⒖闂侀潧妫岄崑鎾绘⒑?
     */
    private int drawTitle(Graphics2D g, int left, int top, int right, Font titleFont) {
        if (!showTitle || title == null || title.trim().isEmpty()) {
            return 0;
        }
        g.setFont(titleFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        int textY = top + metrics.getAscent();
        g.drawString(title, left, textY);
        return metrics.getHeight() + 10;
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啯绻濋崒婊勬闂佸壊鍋呭ú鏍儗濡ゅ懏鐓忓璇″灠閸婂鎯屽Δ鍛拻濞达絿鎳撻婊呯磼鐎ｎ偄鐏撮柟宕囧枛椤㈡盯鎮欓弶鎴烆吅闂備胶绮弻銊╁箟閳╁啰顩查柕鍫濐槹閻撴盯鏌涚仦鍓р槈妞ゆ洘绮庣槐鎺楀焵椤掑嫬鐒垫い鎺戝閳锋垿鏌熼懖鈺佷粶闁告梹姘ㄩ幉鎼佸箥椤旈棿鎴峰┑鐐叉閸ㄨ姤淇婇悿顖ｆХ闂佺顑嗛幑鍥х暦閻戠瓔鏁囬柣鎰椤洘绻濈喊澶岀？闁惧繐閰ｅ畷鍦崉閾忚娈鹃梺璺ㄥ枔婵敻宕戦幇鐗堢厱妞ゎ厽鍨垫禍婵囩箾閸稑鐏叉慨濠傛惈鏁堥柛銉戝喚鐎抽梺璇插閻噣宕￠幎钘夌畺闁汇垹澹婇弫濠囨煠閹帒鍔氶柛鏂挎嚇濮婃椽骞愭惔銏╂⒖闂侀潧妫岄崑鎾绘⒑?
     */
    private int drawLegend(Graphics2D g, int left, int top, int right, Font legendFont) {
        if (!showLegend) {
            return 0;
        }
        g.setFont(legendFont);
        FontMetrics metrics = g.getFontMetrics();
        int x = left;
        int baseline = top + metrics.getAscent();
        int rowHeight = Math.max(metrics.getHeight(), legendMarkerSize) + 6;
        int usedHeight = rowHeight;
        for (int i = 0; i < seriesList.size(); i++) {
            BarChartSeries series = seriesList.get(i);
            String text = Optional.ofNullable(series.getName()).orElse(String.valueOf((i + 1)));
            int textWidth = metrics.stringWidth(text);
            int itemWidth = legendMarkerSize + 6 + textWidth + legendItemGap;
            // 闂備浇宕甸崰鎰垝鎼淬垺娅犳俊銈呮噹缁犱即鏌涘☉姗堟敾婵炲懐濞€閺岋絽螣閾忛€涚驳闂佺顑嗛敃銏ゅ箖濮椻偓閹瑩妫冨☉妤€顥氶梻浣告惈椤﹂亶骞婇幘璇茬叀濠㈣埖鍔曠粻鑽も偓瑙勬礀濞夛箓鏁嶉崟顓狅紲濠德板€愰崑鎾绘煙閾忣個顏堫敋閿濆鏁嬮柍褜鍓熼悰顕€宕卞☉妯奸獓闂佸湱顭堢€垫帡宕崼鏇熲拻濞达絽鎲￠崯鐐烘煛瀹€瀣М鐎规洘绻傞濂稿川椤栨稒顔曢梻浣藉亹閳峰牓宕滃璺虹；婵☆垰鐨烽崑鎾诲礂婢跺﹣澹曢梺璇插嚱缂嶅棝宕滃☉鈶哄洭顢氶埀顒€顫忕紒妯肩懝闁搞儜鍐炬交闂備浇顕х换鎴犳崲閸繄鏆﹂柕蹇嬪€曞洿婵犮垼娉涢敃锕傚磹閻愮儤鈷戦柛娑橈功閹斥偓闂佺姘﹀▍鏇犫偓闈涖偢楠炲洭鎮ч崼婵呮闂備礁婀遍崕銈夊春婵犲洤绀夐柣鏂垮悑閻撴盯鏌涘鈧粈浣圭閺夊簱鏀?
            if (x + itemWidth > right && x > left) {
                x = left;
                baseline += rowHeight;
                usedHeight += rowHeight;
            }
            g.setColor(resolveSeriesColor(series, i));
            int markerY = baseline - metrics.getAscent() + Math.max(0, (metrics.getHeight() - legendMarkerSize) / 2);
            g.fillRoundRect(x, markerY, legendMarkerSize, legendMarkerSize, 4, 4);
            g.setColor(labelColor);
            g.drawString(text, x + legendMarkerSize + 6, baseline);
            x += itemWidth;
        }
        return usedHeight + 4;
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洦顐介柕鍫濇处椤洟鏌￠崶銉ョ仾闁?Y 闂傚倷绀侀幖顐λ囬柆宥呯；婵炴垯鍨洪崑銈夋煏婵炵偓娅嗛柛瀣樀閺屻劌鈹戦崱妯虹獩婵＄偠顫夋繛濠囧蓟閿濆绠涢梻鍫熺☉椤晛顪冮妶蹇曠ɑ闁绘挴鈧剚娼栧┑鐘宠壘閻愬﹪鏌ㄥ┑鍡樺櫢濠㈣娲熷缁樻媴閸︻厽鑿囬梺鍛婃煥閻ジ鍩€椤掍礁鍤柛妯恒偢婵℃挳骞掑Δ浣告疂闂傚倸鐗婄粙鎾剁玻閻愮儤鍋℃繝濠傚暟鏁堝銈冨灪閻熲晛顕ｉ鈧崺鈧い鎺戝缁犳岸鏌￠崘銊у闁哄懏绮撻弻銈吤圭€ｎ偅鐝栧銈忓瘜閸ｏ絽顫忛崫鍔借櫣绱掑Ο琛℃瀼闂備礁鎲￠弻銊╊敄閸ヮ剚鍤嶉梺顒€绋侀弫鍡椕归敐鍫涒偓鈧柡瀣墵濮婃椽骞嗚缁犵増绻濋埀顒佹綇閳哄倹娈板┑鐐村灟閸ㄦ椽鎮￠弴銏＄厪濠㈣埖绋撻悾閬嶆煕閺傝鈧繈骞冨Ο璺ㄧ杸闁挎繂鎳愭禒鎼佹⒑鐠団€虫灍闁稿孩濞婇崺銏℃償閵堝洨鏉搁梺瑙勫劤椤曨厼危濮椻偓閺岋絾鎯旈敍鍕ㄥ┑鐐跺瀹曠數鍒掓繝姘闁哄啫鍊稿畷銉╂⒑缂佹ɑ鈷掗柛搴涘€曢悾閿嬪緞鐎ｎ剛鐦堥梻鍌氱墛缁嬫垿顢旈妶澶嬬厱?
     */
    private int calcYAxisLabelWidth(FontMetrics metrics, List<Double> ticks) {
        int max = 0;
        for (Double tick : ticks) {
            max = Math.max(max, metrics.stringWidth(formatValue(tick)));
        }
        return max;
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啴宕奸姀鈽嗘綗闂佸搫鍟悧鍡欑不閿濆鐓熼柟鎵濞懷勩亜閵夛附绀堢紒杈ㄦ尰閹峰懘鎮烽幍顕呮О闂備浇娉曢崰鍡涘磿閻㈢鏄ラ柍褜鍓氶妵鍕箣閿濆棛銆婇梺?闂傚倷绀侀幖顐λ囬柆宥呯；闁绘劕妯婇悞鑺ョ箾閸℃ɑ灏柡鍕╁劦閺屾洝绠涙繝鍌氣拤闂佺娴烽崰鏍蓟閵娾晛绫嶉柛銉仢閹剧粯鐓熼幖鎼枛濞呭秹鏌″畝瀣瘈鐎规洖銈搁、鏇㈠閻樿尙顔囨繝鐢靛仜閻°劎鍒掑鍥ㄦ殰闁炽儱纾弳锕傛煥濠靛棙顥犵紒鈾€鍋撻梻浣侯焾閺堫剛绮欓幘鍓佺煓闁告洦鍨遍埛鎴︽煕濠靛棗顏€涙繂鈹戦悙瀵搞偞闁哄懐濞€瀵粯绻濋崘鈺冪Ф闂佸啿鎼崯顖炴晬韫囨稒鐓熼柣鏂跨埣濡剧兘鏌涙繝鍐ㄥ鐎?
     */
    private void drawGridAndAxis(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom,
                                 int zeroY, List<Double> ticks, ChartValueRange valueRange, Font labelFont) {
        g.setFont(labelFont);
        FontMetrics metrics = g.getFontMetrics();
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(axisStrokeWidth));
        for (Double tick : ticks) {
            // 婵犵數濮甸鏍闯椤栨粌绶ら柣锝呮湰瀹曟煡鎮楅敐搴℃灍闁绘挸鍊圭换婵囩節閸屾粌顣虹紓浣插亾闁糕剝绋掗崑锝夋煕閵夛絽濡肩紒鑼帛閵囧嫰寮崠陇鍚┑顔硷攻濡炰粙銆佸▎鎴滅剨闁哄诞鍌氼棜闂備胶绮ú鎴︽倿閿曞倹鍎楁俊銈呮噺閳锋垿鏌ｉ悢绋款棆闁伙綆鍙冮弻娑㈠Ω閵夘喚鍚嬮梺璇″枤閸嬫挾鎹㈠┑鍡╂僵妞ゆ帒鍋嗗Σ鎾⒒娴ｇ瓔娼愭い鏃€鐗犲畷鏉款潩鏉堛劍娈板┑鐐村灟閸ㄦ椽鎮￠弴銏＄厪濠㈣埖绋撻悾閬嶆煕閺傝鈧繈骞冨Ο璺ㄧ杸闁挎繂鎳愭禒顓犵磽娴ｅ壊鍎忔い锔炬暬閵嗕礁顫滈埀顒勫箖閳轰胶鏆﹂柛銉ｅ妽椤斿繘姊绘担绛嬪殭濡ょ姷顭堥敃銏ゆ焼瀹ュ懐锛涢梺闈涚墕閹峰鎮楅懜鐐逛簻闁哄稁鍋勬禒婊呯棯閸欍儳鐭欓柣鎿冨亰瀹曞爼濡搁敂缁㈡Ф婵＄偑鍊х粻鎾寸閸洖钃熸繛鎴炵矤濡插ジ姊洪崨濠冣拹闁绘濞€楠炲啴濡烽埡鍌涙珳婵犮垼娉涜癌?
            double ratio = (tick - valueRange.getMin()) / (valueRange.getMax() - valueRange.getMin());
            int y = plotBottom - (int) Math.round(ratio * (plotBottom - plotTop));
            if (showGrid) {
                g.setColor(gridColor);
                g.drawLine(plotLeft, y, plotRight, y);
            }
            g.setColor(labelColor);
            String tickText = formatAxisValue(tick);
            int textWidth = metrics.stringWidth(tickText);
            g.drawString(tickText, plotLeft - textWidth - 10, y + metrics.getAscent() / 2 - 2);
        }
        // 闂傚倸鍊烽懗鍫曪綖鐎ｎ喖绀嬫い鎰╁焺濞兼棃姊绘担铏瑰笡闁绘鍘惧濠囧箚瑜滈悞鑺ャ亜韫囨挾澧曠紒鐙欏洦鐓欓悗娑欘焽缁犳碍銇勯弬娆炬█婵﹥妞藉畷銊︾節鎼淬垻鏆︽繝鐢靛仜瀵墎鍒掓惔銊ョ劦妞ゆ帊绀侀崵顒勬煕濞嗗繐鏆欓柣锝呭槻铻栭柛娑卞幘椤斿懘姊洪崗闂磋埅闁稿氦宕靛Σ鎰板Ω閳哄倵鎷洪梺鍦焾濞寸兘鍩ユ径鎰厸闁割偒鍋勬晶瀛樸亜閵忊槅娈旈柍瑙勫灩閳ь剨缍嗘禍鐐烘儓閸曨垱鈷戦柛婵嗗婢с垽鏌℃担瑙勫€愰柟顖氱灱娴狅妇鎲撮幒鎴﹀弰鐎规洖鐖兼俊姝岊槾闁伙絽銈稿娲传閸曨剦妫忛梺绋款儐閹稿墽妲愰幘璇茬＜婵﹩鍓氬▓鏌ユ⒑閹肩偛濡奸柣鏍с偢楠炲啳顦圭€规洦鍋婃俊鐤槼闁挎稑绻戠换婵嬪閿濆棛銆愰梺纭呭Г缁捇骞冮悜钘夘潊闁靛牆妫涢崢鎼佹⒑缁嬫寧婀扮痪鏉跨Ч楠炴牠骞囬悧鍫㈠幗闂佹剚鍨遍悷銉ッ洪妶鍫㈢?
        if (showAxis) {
            g.setColor(axisColor);
            g.drawLine(plotLeft, plotTop, plotLeft, plotBottom);
            g.drawLine(plotLeft, zeroY, plotRight, zeroY);
        }
        g.setStroke(oldStroke);
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洦顐介柕鍫濇处椤洟鏌￠崶銉ョ仾闁稿鏅涢埞鎴︽偐鐎圭姴顥濋梺鍝勵儎缁舵岸寮婚埄鍐ㄧ窞濠电姴瀚惃鎴︽⒑?0 闂傚倷娴囬褍霉閻戣棄鏋侀柟闂寸缁犵娀鏌熼悙顒€鍔跺┑顔藉▕閺岋紕浠︾拠鎻掑闂?Y 闂傚倸鍊烽懗鍫曪綖鐎ｎ喖绀嬫い鎰╁焺濞兼棃姊绘担铏瑰笡闁绘鍘剧槐鐐存媴闁稓绠?
     * 闂傚倸鍊烽懗鍫曗€﹂崼銏″床闁割偁鍎辩粈澶屸偓骞垮劚椤︿粙寮崱妯肩瘈濠电姴鍊绘晶鏇㈡煛鐎ｂ晝绐旈柡宀嬬到铻栭柍褜鍓熼幃褎绻濋崶浣告穿椤﹀綊鏌＄仦鍓ф创闁糕晪绻濆畷鐔碱敆婢跺鎽嬮梻鍌欑劍閹爼宕愰弴銏＄厐闁挎繂鎳愰弳锕傛煟閹达絾顥夐幆鐔兼⒑闂堟冻绱￠柛灞剧懃椤掋垽姊婚崒姘偓椋庢閿熺姴闂い鏇楀亾闁诡喒鍓濆鍕沪缂併垺缍楁繝鐢靛█濞佳囶敄閸涱垳鐭嗛柛宀€鍋為悡蹇撯攽閻愯尙浠㈠┑鈩冨閵囧嫰鏁傞崹顔肩ギ闂佸搫澶囬崜婵嬪箯閸涙潙浼犻柛鏇炵仛濮ｅ绻濈喊澶岀？闁稿鐩畷鎰板垂椤旂偓娈惧┑鐐叉▕娴滄繈骞嗛悙鐑樼厽闁绘柨鎼。濂告煟閵堝懏鍠樻慨濠冩そ濡啴鍩￠崟顓фЧ闂備礁鎲″褰掑垂閹稿簼绻嗛悗娑欘焽缁♀偓闂佹悶鍎崝搴ㄥ礉缁嬫娓婚柕鍫濋楠炴鎮介婊冧户缂侇喖鐗婂鍕偓锝庡亜瀵灝鈹戦悩鑼粵闁圭澧界划鏃堫敆閸屾粎锛滈梺閫炲苯澧伴柍褜鍓ㄧ紞鍡涘焵椤掑﹦鐣卞Δ鐘虫倐閹箖鏁撻悩鍐蹭罕闂佸壊鍋呯缓楣冨磻閹捐围濠㈣泛顑囬崢鎼佹⒑閸涘﹤濮﹂柛妯恒偢閹繝骞囬悧鍫濃偓鐢告煕椤垵浜為柣顓烇功閹喖鈻庨幘瀵稿幐闂佺鏈懝楣冨煕閺傛５褰掑礂閻撳骸顫掗梺璇″枦椤绮悢鍝ョ瘈闁告洦鍙忕槐鐢告⒒娴ｉ涓茬紒韫矙閳ワ箓鏌ㄧ€ｂ晝绠?
     */
    private int calculateZeroY(int plotTop, int plotBottom, int plotHeight, ChartValueRange valueRange) {
        if (valueRange.getMin() >= 0) {
            return plotBottom;
        }
        if (valueRange.getMax() <= 0) {
            return plotTop;
        }
        double baselineRatio = (0D - valueRange.getMin()) / (valueRange.getMax() - valueRange.getMin());
        return plotBottom - (int) Math.round(plotHeight * baselineRatio);
    }

    /**
     * 闂傚倸鍊风粈浣革耿闁秮鈧箓宕奸妷瀣喘椤㈡稑顭ㄩ崨顖ｆЦ闂佽鍑界紞鍡涘窗閺嶎偆鐭嗛柛顐熸噰閸嬫捇鐛崹顔煎闂佸搫顑囬崕銈夊箯閸涙潙绀堥柟缁樺笚椤撳潡姊绘繝搴′簻婵炶绠撻獮鎰板箹娴ｇ懓鈧潧鈹戦悩宕囶暡闁绘挻娲樼换娑㈠幢濡搫顫岄梺璇茬箚閺呮繄妲愰幒妤婃晪闁告侗鍘炬禒鎼佹倵鐟欏嫭绀冪€光偓缁嬭法鏆﹀┑鍌滎焾閸楄櫕淇婇妶鍌氫壕婵炲瓨绮撶粻鏍蓟閿熺姴绀嬫い鎰╁€楅濠囨煟閵忊晛鐏犳俊顐ｇ箞瀵?
     * `stacked=true` 闂傚倸鍊风粈渚€骞栭锕€鐤い鎰剁稻濞呯娀骞栨潏鍓у埌闁搞劍绻冮妵鍕冀椤愵澀绮剁紓浣插亾闁糕剝绋掗崑锝呂旈敂钘夘嚋妞ゅ繒濞€閺屾盯濡搁妷顔惧悑闂佸搫鏈粙鎴﹀煝鎼淬劌绠ｆ繝銏╁墾缂嶄線寮婚敐澶婄閻庢稒锚閸炲姊烘潪鎵槮缂佸鍩栫粋鎺楁晝閸屾稑娈熼梺闈涱槶閸庤鲸鏅舵ィ鍐┾拻濞达絽鎲￠崯鐐烘偨椤栨侗娈滈柟顔炬焿椤︽挳鏌涢幒鎾虫诞妞ゃ垺鐩幃娆撴嚑椤掑孝闂備浇宕垫慨鎾箹椤愶箑绠规い鎰堕檮閸婄敻鏌ゆ慨鎰偓妤冨閽樺褰掓晲閸ュ墎鍔稿銈呮禋娴滎亪寮婚敐澶婄閻庢稒锚閸炲绻?
     */
    private void drawBars(Graphics2D g, int plotLeft, int plotTop, int plotRight, int plotBottom, int zeroY,
                          int plotWidth, int plotHeight, ChartValueRange valueRange, Font valueFont) {
        if (stacked) {
            drawStackedBars(g, plotLeft, plotTop, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
            return;
        }
        drawGroupedBars(g, plotLeft, plotBottom, zeroY, plotWidth, plotHeight, valueRange, valueFont);
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啯绻濋崒婊勬濠殿喗顭堝▔娑㈢嵁閵忥紕绠鹃柛鈩兠悘鈺呮煟閹捐尙绐旀慨濠冩そ瀹曨偊宕熼鐘插О濠电偛鐡ㄧ划鐘诲礈濮樿泛绠為柕濞炬櫅缁犵懓霉閿濆懏鎲搁柛濠勫仦缁绘盯鏁愰崨顔碱槱婵犫拃鍐弰鐎?
     * 闂傚倸鍊风粈渚€骞夐敓鐘冲殞闁绘劦鍓涢梽鍕煛閸愩劌鈧宕″鑸靛€甸梻鍫熺⊕閹叉悂鎮樿箛鏇熸毈闁哄被鍊栭幈銊╁箛椤戣棄浜鹃柡宥庡亞閻滅粯淇婇妶鍌氫壕闂佸疇妫勯ˇ鐢哥嵁濮椻偓楠炲洦鎷呯粙鍨棊闂傚倷妞掔槐顕€鎳欒ぐ鎺戠婵犻潧鐗忛惌娆忣熆閼搁潧濮﹂柡浣稿€块弻娑㈠焺閸愵亖妲堥梺鍛娚戦悷鈺侇潖濞差亜宸濆┑鐘插€婚悷鎻掝渻閵堝啫濡奸柣妤€绻橀崺鈧い鎺嶇閸ゎ剟鏌涘Ο鍝勨挃闁告帗甯￠崺锟犲礃閿濆懍澹曢梺鎸庣箓妤犲憡绂嶅鍫熺厽闊洦鎸婚崰姗€鏌″畝瀣？濞寸媴绠撻獮鍡氼槼闁哄棙澹嗙槐鎾存媴娴犲鎽电紓浣筋嚙閻楁捇骞冩导鎼晪闁逞屽墴楠炲﹤顭ㄩ崼鐕佹濠电偞鍨剁喊宥夊煝鎼淬劍鈷掗柛灞捐壘閳ь剚鎮傚畷鎰節濮ｇ顦甸獮鍥敊閸撗冪婵犵數鍋為崹鍫曟偡瑜旈幃椋庢喆閸曨厾鐦堥梻鍌氱墛缁嬫帞绮婇埡鍛厱閻庯絾鏌ㄥú锕傛偂濞嗘挻鐓熼柟瀵镐紳椤忓棛绠旈柟鐑樻⒒绾惧ジ寮堕崼娑樺闁诲繑娼欓埞?
     */
    private void drawGroupedBars(Graphics2D g, int plotLeft, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ChartValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        int seriesCount = seriesList.size();
        BarChartLayoutCalculator.GroupedLayout layout = LAYOUT_CALCULATOR.calculateGrouped(
                plotLeft, plotWidth, categoryCount, seriesCount,
                categoryGapRatio, barGapRatio, minBarWidth, maxBarWidth
        );
        // 濠电姷鏁告慨鐑姐€傛禒瀣劦妞ゆ巻鍋撻柛鐔锋健閸┾偓妞ゆ巻鍋撶紓宥咃躬楠炲啫螣鐠囪尙绐為悗鍏夊亾闁逞屽墰娴滄悂鎮介崨濠勫幘婵犳鍠楅崝鏇㈠焵椤掍胶澧辩紒顕嗙到铻栭柛娑卞枓閹峰姊虹粙鎸庢拱缂侇喚濮磋灋閹艰揪绲跨壕濂告煙闁箑骞橀柍顖涙礃缁绘盯鎮℃惔顔惧悑閻庤娲橀崕濂杆囬弶妫电懓顭ㄩ幇顔拘滈梺璇″枟椤ㄥ棝骞忛崨鏉戠妞ゎ厽鍨堕ˉ宥夋⒒娴ｈ鍋犲ù鐓庢喘瀹曘劑顢涘鎰簥闂傚倷绀侀幉鈥趁洪敃鍌氱；闁圭儤顨呴悿顔锯偓骞垮劚椤︻垳绮昏ぐ鎺戠骇闁割偅绻傞埛鏃堟煟閿濆鎲鹃柡宀嬬秮閳ワ箓骞嬪┑鍡╂浇缂傚倷绀侀崐鍝ョ矓閻㈢绠氶柡鍐ㄧ墛閺呮煡鏌涢妷锝呭闁稿寒鍨伴埞?0闂傚倸鍊烽悞锔锯偓绗涘懐鐭欓柟杈鹃檮閸ゆ劖銇勯弽顐粶闁搞劌鍊归妵鍕冀椤愵澀绮堕梺缁樺姇閿曨亪寮婚敐澶婄疀妞ゆ牗菤閸嬫捇鍩€椤掑嫭鐓曢悗锝庡亝瀹曞矂鏌涢埞鎯т壕婵＄偑鍊栫敮濠囨嚄閸洖鐓濋柡鍐ㄧ墛閻擄綁鐓崶銊﹀闁衡偓閻楀牄浜滄い鎰靛墰閻ｇ敻鏌″畝瀣М闁糕斁鍓濈换婵嬪磼濞戞矮閭┑锛勫亼閸婃牕顫忛悷閭︽綎鐟滅増甯掔粻姘舵煛閸愩劎澧涢柡鍛矒閺岋綁骞囬棃娑橆潾闂?80%闂?
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                BarChartSeries series = seriesList.get(seriesIndex);
                double value = series.getValues().get(categoryIndex);
                double normalized = Math.abs(value) / (valueRange.getMax() - valueRange.getMin());
                int barHeight = (int) Math.round(normalized * plotHeight);
                int barWidth = layout.getBarWidth();
                int x = layout.resolveBarX(categoryIndex, seriesIndex);
                // 闂傚倸鍊风粈渚€骞夐敍鍕殰闁圭儤鍤﹀☉妯锋婵﹩鍓欓悘濠囨倵楠炲灝鍔氶柟鍙夛耿閹垽鎮℃惔锝勭礈闁诲氦顫夊ú鏍洪妸鈹库偓渚€宕ㄩ鍓ь啎闂佺懓顕崑鐐典焊椤撶喆浜滈柟瀵稿仜閻忊晜淇婇崣澶婂闁宠鍨垮畷閬嶅煛閸屾稏鈧啴姊绘担鐟邦嚋缂佽鍊块獮濠冨緞閹邦剛锛涢梺鐟板⒔缁垶宕戦崒鐐茬婵烇綆鍓欓悘鈺呮煃瑜滈崜婵嬵敋瑜旈崺銉﹀緞閹邦剛顔掔紓浣圭〒閹虫挻鎱ㄥ☉銏♀拺閺夌偞澹嗛崝宥夋煙閻熺増鎼愭い顐㈢箺閵囨劙骞掗幋鐙€妲洪梻浣瑰缁嬫垹鈧凹鍣ｈ棟妞ゆ洍鍋撻柡宀嬬秮閹晠宕橀幓鎺濇綍闂備礁鎽滄慨鎾煀閿濆鏄ラ柍褜鍓氶妵鍕箳閹存績鍋撻崫銉︽殰闁割偅娲橀悡娆愩亜閺冨倹娅曢柣顓烆儑缁辨帞鈧綆浜跺Σ娲煃瑜滈崜銊х礊閸℃稑绀堟慨妯挎硾閻ら箖鏌熼梻瀵稿妽闁抽攱甯￠弻宥堫檨闁告挻鐩獮澶娢旈崨顓囇囨煕鐏炲墽鐓?
                int y = value >= 0 ? zeroY - barHeight : zeroY;

                g.setColor(resolveSeriesColor(series, seriesIndex));
                fillBar(g, x, y, barWidth, barHeight, true, true);

                if (showValueLabel) {
                    drawValueLabel(g, valueFont, value, x, y, barWidth, barHeight, zeroY);
                }
            }
        }
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啯绻濋崒婊勬婵犻潧鍊婚…鍫ョ嵁閵忊€茬箚闁靛牆鍊告禍鍓х磽閸屾氨孝闁活厼鍊垮濠氬灳瀹曞洦娈曢梺鍛婂壃閸愨晛鍓垫繝鐢靛У椤旀牠宕板Δ鍛畺闁稿本姘ㄩ弳锕傛煠閸濄儲鏆╅柍鐟扮Ч閺岀喖宕楅崫銉М婵?
     * 婵犵數濮甸鏍窗濡ゅ啯宕查柟閭﹀枛缁躲倕霉閻樺樊鍎忛柣鎺戠仛閵囧嫰骞掗幋婵冨亾閸濄儲鏆滈柛顐ｆ礃閻撴瑩姊洪崹顕呭剰妞ゃ儱顑夐弻鐔风暋閹殿喚楔闂佽鍠撻崹缁樼閸涘﹥濯撮梻鈧幇顒夋闂傚倷鑳剁划顖炲箰閸涘娈介柟闂寸劍閸婇潧鈹戦悩宕囶暡闁绘挻娲熼弻鐔兼倻濡偐鐣垫繛瀛樼矋椤ㄥ懘婀?0 濠电姷鏁搁崑鐐哄垂閸洖绠伴柟闂寸缁犺銇勯幇鍫曟闁稿瀚伴弻宥堫檨闁告挻绋撳Σ鎰板箳濡も偓缁犳氨鎲歌箛鏃傤浄闁靛繈鍊栭悡娆愩亜閺冨倹娅曢柣顓烆儑缁辨帞鈧綆浜跺Ο鈧繝纰樷偓宕囧煟鐎规洜鍠栭、妤佹媴閸濆嫬鈻忛梻鍌氬€风粈浣圭珶婵犲洤纾婚柛鏇ㄥ€犲☉妯滄棃宕熼埡鈧Ч妤呮⒑閸濆嫬鏆欓柣妤€锕鏌ヮ敆閸屾浜鹃柛蹇擃槸娴滈箖姊洪柅鐐茶嫰婢ь噣鏌嶇拠鑼у┑鈩冩倐閸┾剝鎷呮笟顖浶㈤梻鍌欒兌绾爼宕滃┑瀣р偓锕傚炊椤剨缍侀弻銊р偓锝傛櫇缁犳艾顪冮妶鍡欏闁靛洦鐩畷鎴﹀箻瀹曞洦娈鹃梺鎼炲劗閺呪晠寮ㄩ妸銉㈡斀闁绘劖娼欓悘锕傛煙閸涘﹥鍊愭い銏＄墵瀹曠喖顢橀悤浣圭稐濠电偞娼欓崥瀣焽濞嗘垹鐭嗛柛宀€鍋為悡鏇熴亜閹扳晛鈧洟寮告惔銊ョ闁哄鍩婇崝鐔虹磼鏉堛劌娴柟顔规櫊閹瑩骞嬮幒鎾冲闂備浇宕垫慨鐢稿礉濡ゅ懎绐楅柡宥庡幖缁犳岸鏌涘畝鈧崑娑氱不濮樿鲸鍠愮€广儱顦伴崐鐢电磽娴ｈ偂鎴﹀矗韫囨稒鐓ユ繝闈涙椤ョ偤鏌ｉ幘鍗炲姦闁哄瞼鍠栭、娆戠驳鐎ｎ偆鏉归柣搴ゎ潐濞叉﹢鏁嬪銈嗘尭閵堢鐣烽崼鏇炵厸濞达絼璀﹂崑褔姊婚崒娆戭槮闁硅绱曢弫顕€鎮㈤搹鍦槱婵炴潙鍚嬪娆撴倿?
     */
    private void drawStackedBars(Graphics2D g, int plotLeft, int plotTop, int plotBottom, int zeroY,
                                 int plotWidth, int plotHeight, ChartValueRange valueRange, Font valueFont) {
        int categoryCount = categories.size();
        BarChartLayoutCalculator.StackedLayout layout = LAYOUT_CALCULATOR.calculateStacked(
                plotLeft, plotWidth, categoryCount, categoryGapRatio, minBarWidth, maxBarWidth
        );

        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            int barWidth = layout.getBarWidth();
            int x = layout.resolveBarX(categoryIndex);
            // 婵犵數濮甸鏍窗濡ゅ啯宕查柟閭﹀枤缁€濠囨煃瑜滈崜娑氭閹烘鐒垫い鎺戝€甸崑鎾绘晲鎼粹剝鐏堢紒鐐劤缂嶅﹪寮婚敐澶婄闁瑰墎鐡旈埀顒侇殜閺岋絾鎯旈埥鍡欏悑闂佸搫鏈惄顖炵嵁閹烘垟鏀介柛鈩冩礈閺佹牠姊绘担鍦菇闁稿鍊濆畷鏉款潩鐠鸿櫣顔嗛柣搴秵閸犳牠鎮￠敓鐘崇厱婵炴垵宕悘锝嗐亜閿旇娅嶆慨濠勭帛缁楃喖宕惰椤晠姊虹拠鑼缂佽鐗嗛悾鐑藉閵堝憘褍顭跨捄鐚撮練鐞氭繈姊虹拠鑼闁稿濞€瀹曟垿骞囬柇锔芥櫆闁哄鐗冮弲婵堝閸忕浜滈柟鎵虫櫅閸旀岸鏌涘鎰佹綈缂佺粯鐩悡顐︻敇閻愯尙銈柣搴ゎ潐濞插繘宕曢搹顐ゎ浄闁挎洖鍊归崐閿嬨亜閹烘埈妲圭悮褔姊婚崒娆戭槮闁硅绻濋幃褔寮撮姀鐘电枃闁瑰吋鐣崝宀€绮诲鑸电厱妞ゆ劧绲跨粻銉︾箾闂傛潙宓嗛柡灞诲妼閳规垿宕卞▎蹇婂彙婵＄偑鍊х紓姘跺础閹惰棄钃熸繛鎴炵懅缁♀偓闂佺鏈喊宥夊磹椤栨埃鏀介柨娑樺娴犳粓鏌涙繝鍌涘仴妤犵偛妫濆畷鐔碱敍濞戞帗瀚婚梻浣哥秺閸嬪﹪宕抽敐澶嬪€垫繛宸簼閻撶喖鏌ｉ弬鎸庡暈缂佲偓閳ь剙鈹戦埥鍡椾簼缂佸鍨归崣鍛節閻㈤潧孝婵炲眰鍊涚换?
            double positiveBase = 0D;
            double negativeBase = 0D;
            double positiveTotal = getCategoryPositiveTotal(categoryIndex);
            double negativeTotal = getCategoryNegativeTotal(categoryIndex);

            int positiveCount = countVisibleSegments(categoryIndex, true);
            int negativeCount = countVisibleSegments(categoryIndex, false);
            int positiveIndex = 0;
            int negativeIndex = 0;

            for (int seriesIndex = 0; seriesIndex < seriesList.size(); seriesIndex++) {
                BarChartSeries series = seriesList.get(seriesIndex);
                double value = series.getValues().get(categoryIndex);
                // 0 闂傚倸鍊烽懗鍫曗€﹂崼銉︽櫇闁靛牆顦憴锕傛倵閿濆骸娅橀柡浣革躬閹﹢鎮欓幓鎺嗘寖闂佸磭绮濠氬焵椤掆偓缁犲秹宕曢崡鐏绘椽濡歌閺嗘粍绻涢幋娆忕仾闁绘挻娲熼弻鐔兼倻濡顦╅梺鍝ュ枎濞层劎妲愰幒鎾寸秶闁靛ě鍛澖闁诲氦顫夊ú鈺冪礊娓氣偓閵嗕礁鈽夊鍡樺兊濡炪倖甯婄欢鈩冪椤撱垺鈷戦悹鍥皺缁犳壆绱掔紒妯虹伌鐎规洖婀遍幑鍕媴閺囩喓娲村┑陇鍩栧鍕節閸曨厽婢戦梻鍌氼煬閸嬪嫬煤閵堝悿褰掓倻閼恒儱浠鹃梺鍛婄☉閻°劑鎮￠悢鍏肩厪濠电偛鐏濋崝姘亜韫囷絼閭柡宀嬬稻閹棃顢欓悡搴骄缂傚倷娴囨ご鍝ユ暜閳ュ磭鏆﹂柛妤冨亹閺嬪酣鐓崶銊︹拻闁哄鍟换婵嬫偨闂堟稐绮堕梺缁橆殔缁绘ê鐣烽幋锔芥櫜濠㈣泛锕﹂崝?
                if (Double.compare(value, 0D) == 0) {
                    continue;
                }
                double displayValue = value;
                if (percentStacked) {
                    // 闂傚倸鍊峰ù鍥儍椤愶箑绀嬫い鎰╁灩琚橀梻鍌欑劍閹爼宕濈仦缁㈡婵繂鐭堥弳浼存⒒娴ｅ憡鎯堥柛濠呮閳绘棃寮撮姀鐘茬€繝鐢靛У绾板秹鎮￠弴鐔虹闁糕剝锚閻忋儵鏌嶇拠鑼缂佺粯鐩獮姗€宕樺顔芥闂備胶顢婄亸娆愭櫠濡ゅ懎绠柛娑樼摠閸嬶繝鏌熷▓鍨灍闁宠閰ｉ弻锝夋偄閸濄儳鐓傛繝鈷€鍕垫疁妤犵偞鍔欏畷銊︾節閸愨晝褰块梻浣虹帛閿氭俊顖氾工椤洦瀵肩€涙鍙嗗┑鐘绘涧濡瑩宕冲ú顏呯厱?闂傚倷娴囧畷鍨叏閻㈢绀夋俊銈呮噹缁愭骞栧ǎ顒€濡肩紒鐘崇墬缁绘繃绻濋崒婊冾暫缂備胶濮甸崹鐢稿煘閹达附鍋愰悗鍦Т椤ユ繂鈹戦悙鍙夊櫡闁稿﹥顨婇獮澶愬箹娴ｇ懓浜遍梺鍓插亝缁诲嫰鎮烽幇顔剧＝濞达綀顫夐埛鎰版煙缁嬪灝鏆遍柣锝囧厴閹晝鎷犻幓鎺戞暏闂備線娼чˇ顓㈠垂鐟欏嫮顩?
                    double divisor = value > 0 ? positiveTotal : Math.abs(negativeTotal);
                    if (Double.compare(divisor, 0D) != 0) {
                        displayValue = value / divisor * 100D;
                    } else {
                        // 闂傚倸鍊峰ù鍥敋閺嶎厼绀堟繛鍡樻尨閳ь剨绠撻幃婊堟寠婢舵劖锛楅梻浣虹帛閸ㄥ爼鈥﹂崶顒€鐓?0 闂傚倸鍊风粈渚€骞栭锕€鐤柟鎯版閺勩儵鏌″搴ｄ粶婵炴垯鍨瑰洿闂佸憡渚楁禍婵嬫倶閸愵喗鈷戦柛婵嗗缁€灞句繆閹绘帗鍤囩€规洏鍎甸崺鈧い鎺戝€荤壕钘壝归敐鍛儓閺嶁€愁渻閵堝啫濡奸柟鍐查叄閹箖鎮滈挊澶岄獓闂佸摜鍠庨崯顖滄暜閳ユ剚鍤曟い鏇楀亾妤犵偞锕㈠畷婊勭瑹閸ャ剱銉╂⒒閸屾瑨鍏岄柛搴ｆ暬瀵彃鈽夐姀鈥充槐闂侀潧臎閸曨剚顔囬梻浣告贡閾忓酣宕伴弽顐や笉?0 濠电姷鏁告慨浼村垂閻撳簶鏋栨繛鎴炲焹閸嬫挸顫濋悡搴㈢彎濡ょ姷鍋涢崯顖滄崲濠靛鐐婇柕濠忛檮閻濐偊鏌ｆ惔鈥冲辅闁稿鎹囬弻娑㈠即閵娿倗鏁栫紓浣介哺瀹€绋款潖閾忓湱鐭欓悹鎭掑妿椤斿洨绱撴担鍓叉Ш闁硅櫕鎹囬崺銏狀吋閸℃ê鍔呴梺闈涱煭閼靛綊骞愰崨瀛樷拻濠电姴楠告晶顖滅磼娴ｈ灏︾€?
                        displayValue = 0D;
                    }
                }

                double startValue;
                double endValue;
                boolean positive = displayValue > 0;
                if (positive) {
                    startValue = positiveBase;
                    positiveBase += displayValue;
                    endValue = positiveBase;
                } else {
                    startValue = negativeBase;
                    negativeBase += displayValue;
                    endValue = negativeBase;
                }

                int segmentTop = valueToY(endValue, plotTop, plotBottom, valueRange);
                int segmentBottom = valueToY(startValue, plotTop, plotBottom, valueRange);
                int y = Math.min(segmentTop, segmentBottom);
                int barHeight = Math.abs(segmentBottom - segmentTop);
                if (barHeight <= 0) {
                    barHeight = 1;
                }

                // 闂傚倸鍊搁崐椋庢閿熺姴纾婚柛娑卞枤閳瑰秹鏌ц箛姘兼綈鐎规洘鐓￠弻娑㈠箛閻㈤潧甯ュ┑鐐烘？閸楁娊寮婚妸銉㈡斀闁糕剝鐟ラ埅鍗烆渻閵堝繒绱伴柛妤佸▕瀵鏁撻悩鑼紲濠电姴锕ょ€氼剙鈻撳畝鍕拺閻犲洠鈧櫕鐏嶇紓渚囧枟閹告悂鎮惧畡閭︽僵閻犲搫鎼懓鍨攽鎺抽崐鎰板磻閹剧粯鐓熼柟鐑樺灟閸嬨垽鏌＄仦鍓ф创妤犵偞甯為埀顒婄秵娴滄牠宕戦幘璇插唨妞ゆ挾鍋犻幗鏇㈡⒑閹肩偛鍔村ù婊勵殕缁傚秷銇愰幒鎾跺幍闂佽鍨庨崘鈺傜槪婵＄偑鍊戦崐鏇㈠箠韫囨洘宕叉繛鎴欏灩闁卞洦绻濋棃娑氬缂侇噣娼ч埞鎴﹀灳閸愯尙楠囬梺璇″枛閸婃悂鎮鹃悜钘壩ㄩ柍鍝勫€瑰▍鍡涙煟閻樺厖鑸柛鏂块閻☆厽绻濋悽闈涗粶闁宦板妿閸掓帞浠︾紒銏☆啍闂佸綊妫跨徊鎼佸炊椤掍礁娈ラ梺闈涚墕鐎氼參宕熼崘顔解拺缂佸娉曢悞鍨攽閳ヨ櫕宸濈紒顔肩墛瀵板嫭绻涢幒鎴犵Ш闁诡喒鏅犲畷锝嗗緞婵犲啯鏆橀梻鍌欑窔閳ь剛鍋涢懟顖涙櫠椤曗偓閺屻倕煤鐠囪尙浠搁悗瑙勬磻閸楀啿鐣烽妸鈺婃晬婵°倐鍋撳ù婊堢畺閹嘲鈻庤箛鎿冧紓婵犮垼顫夐敃銏ゅ蓟閻斿憡鍙忛柟閭﹀墰娴犲墽绱撴担鍓插剰妞わ妇鏁诲畷娲焵椤掍降浜滈柟鐑樺灥閳ь剙顭峰畷鈥愁潩椤撴粈绨婚梺鍝勭▉閸嬪嫭绂掗敃鈧湁闁绘顒查懓鍧楁煙椤旀儳鍘寸€殿喗娼欓～婵囶潙閺嶃剱婊勭節閻㈤潧浠﹂柟绋挎啞閺呰埖绂掔€ｎ亞鐤呴梺鍦檸閸犳牜绮婚幎鑺ュ€甸柨娑樺船閸熲晜绔?
                boolean roundStart = positive ? positiveIndex == positiveCount - 1 : negativeIndex == 0;
                boolean roundEnd = positive ? positiveIndex == 0 : negativeIndex == negativeCount - 1;
                if (positive) {
                    positiveIndex++;
                } else {
                    negativeIndex++;
                }

                g.setColor(resolveSeriesColor(series, seriesIndex));
                fillBar(g, x, y, barWidth, barHeight, roundStart, roundEnd);

                if (showValueLabel) {
                    double percent = resolveStackSegmentPercent(value, positiveTotal, negativeTotal);
                    drawStackedValueLabel(g, valueFont, value, percent, x, y, barWidth, barHeight, positive, zeroY);
                }
            }
        }
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啯绻濋崒婊勬闂佽法鍠撴慨鎾础閹惰姤鐓熼柡鍌涘閹牊銇勯妷锕€鐏存慨濠冩そ瀹曨偊宕熼纰变純闁荤喐绮嶅姗€藝鏉堚晜顫曢柣鎰暯閸嬫捇鏁愭惔鈩冪亶闂佸搫鎷嬮崜鐔煎箖濮椻偓閹瑥霉鐎ｎ亙澹曢梺缁樻椤ユ捁銇愰弻銉︹拻濞达絽鎲＄拹鈩冦亜椤撶偟澧﹂挊婵嬫⒒閸喓鈼ら柍褜鍓氱敮鈥崇暦濠婂嫭濯撮柣鐔哄濮ｅ洤鈹戦悙鑸靛涧缂佹彃娼￠獮濠囧箻鐎靛壊娴勫┑顔姐仜閸嬫捇鏌＄仦鍓ф创妤犵偞甯￠獮瀣偐閼碱剙骞楅梻鍌欑窔濞佳兾涘Δ鍛櫇妞ゅ繐鐗嗛拑?闂傚倷绀佸﹢閬嶅储瑜旈幃娲Ω閵夊啯妞介幃銏ゆ偂鎼达綇绱甸梻浣告贡閾忓酣宕伴弽顓炵闁割偅娲橀悡鏇㈡煃閳轰礁鏆欓柛銈庡墯缁绘稓娑垫搴ｇ槇闂?
     */
    private void fillBar(Graphics2D g, int x, int y, int barWidth, int barHeight, boolean roundTop, boolean roundBottom) {
        if (barHeight <= 0 || barWidth <= 0) {
            return;
        }
        // 濠电姷鏁搁崑鐐哄垂閸洖绠伴柛婵勫劤閻捇鏌ｉ悢璇茬劷闁逞屽墯鐢€崇暦濠婂嫭濯撮柣鐔哄濮ｅ洤鈹戦悙鑸靛涧缂佽弓绮欓獮澶愬焺閸愌呯畾闂佹悶鍎洪崜姘跺磻閳╁啰绠鹃柛鈩兠慨鍥╃磼濡や胶顣插ǎ鍥э躬閹瑩骞撻幒鍡椾壕闁秆勵殔閺嬩胶鈧箍鍎遍幊鎰板汲閸℃稒鐓冪憸婊堝礈濞嗘垵寮查梻浣侯潒閸曞灚鐣剁紓浣哄У婵炲﹪寮婚弴鐔风窞闁割偅绻傞～鍛攽閻愬樊妲归柣鎿勭節瀵鏁愭径妯绘櫍濠电偞鍨堕悷锕傛偟閺冨牊鈷戦柣鎾冲閹茬顭胯椤ㄥ牓骞戦姀銈呭窛闁圭⒈鍘介弲婵嬫⒑闂堟稓绠冲┑顔碱嚟缁參顢橀姀鈾€鎷洪梺鍛婄☉椤剙鈻撻弮鍫熺厱闁绘棁顕ч崝鐢告煏閸ャ劌濮嶇€殿喖顭锋俊鐑藉Ψ?
        if (barArc <= 0 || (!roundTop && !roundBottom)) {
            g.fillRect(x, y, barWidth, barHeight);
            return;
        }
        if (roundTop && roundBottom) {
            g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, barArc, barArc));
            return;
        }

        // 闂傚倸鍊风粈渚€骞夐敓鐘冲仭妞ゆ牗绋撻々鍙夌箾閸℃ê鐏︾€规洖寮堕幈銊ノ熼崹顔惧帿闂佺顑傞弲婊呮崲濞戙垹骞㈡俊銈勭劍瀹曞啿顪冮妶鍛濞存粠鍓涘Σ鎰板箳濡も偓缁秹鏌熺€涙绠樺ù鐘櫅閳规垿鎮欑€涙ê纰嶆繝鈷€鍌滅煓妤犵偛顦甸獮姗€顢欓懖鈺婂敽闂備礁婀遍崑鎾诲箚鐏炶娇锝夘敍閻愮补鎷洪梺鍦焾濞寸兘鍩ユ径鎰厾婵炶尪顕ч悘锔锯偓瑙勬磸閸ㄥ綊鍩ユ径鎰潊闁炽儱鍘栫紓鎾绘⒑閼姐倕孝婵炲眰鍊曢锝夋嚋閸忓摜绠氶梺鎼炲労閸撴岸宕戦埄鍐闁糕剝锚婵洨绱掑锕€娲﹂悡鐔兼煙缂併垹鐏℃繛鍛墦閺屾洟宕堕妸銉ヮ潚闂佹寧绻勯崑銈夈€佸▎鎾崇鐟滄粌危閻戣姤鈷掑ù锝呮啞閸熺偤鏌涢弮鈧ú婊呭垝閺冨牊鍋い鏍ㄥ喕缁辨挻绻濋悽闈涗沪闁搞劌鐖奸獮鎰板箻椤旇偐锛涢梺瑙勫劤椤曨厾绮绘ィ鍐╃厱妞ゆ劗濮撮悘顏堟煛閸″繑娅嗙紒缁樼洴閹崇娀鎳滈悽鐐光偓濠囨⒑閻熸澘绾ч柟鍛婂▕閻涱噣骞掑Δ鈧猾宥夋煕鐏炴崘澹樼紒鎻掔秺閺岋綁鎮㈤崫銉х厔闂佺硶鏅涘ú顓炵暦鐟欏嫮闄勯柟鑲╁亹閸嬫捇宕ㄩ幖顓熸櫇闂佹寧妫佸Λ鍕礈椤曗偓濮婃椽宕崟顔炬闂佸憡鍔︽禍鐐哄煟閵夈儮鏀介柣鎰綑閻忥箓鏌熼崨濠冨唉鐎规洘鍨块獮姗€宕滄担鐚寸床闂備礁鎼崯顖炲垂閻㈠憡鍊舵繛鎴欏灪閻?
        int arcInset = Math.min(barHeight / 2, Math.max(1, barArc / 2));
        if (roundTop) {
            g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, barArc, barArc));
            if (barHeight > arcInset) {
                g.fillRect(x, y + arcInset, barWidth, barHeight - arcInset);
            }
            return;
        }

        g.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, barArc, barArc));
        if (barHeight > arcInset) {
            g.fillRect(x, y, barWidth, barHeight - arcInset);
        }
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍤勯柛顐ｆ礀缁愭鏌￠崶銉ョ労闁轰礁娲弻鐔兼⒒鐎靛壊妲紓浣哄С閸楁娊寮诲☉銏犖ㄩ柟瀛樼箖閸ｇ儤鎱ㄩ敐鍛崳缂佽鲸鎸荤粭鐔煎炊瑜岄崙浠嬫倵鐟欏嫭绀嬫繛浣冲懎寮查梻浣虹帛椤ㄥ懘鎮ц箛娑樼厺闁哄啫鐗婇崐鐢告煥濠靛棝顎楀ù婊€绮欓弻娑㈠棘閸濆嫪澹曢梻鍌氬€风粈渚€骞夐敍鍕瀳鐎广儱顦崹鍌炴煕椤愶絾绀€闁搞劌鍊块弻鏇熺箾瑜嶇€氼剟顢旈幖浣光拺缂備焦蓱椤ュ牓鏌￠埀顒勬焼瀹ュ懐锛欐繝鐢靛У绾板秹鎮￠悢鍏肩厪濠电偛鐏濋崝姘亜韫囷絼绨肩紒缁樼箞閸┾偓妞ゆ帊鑳堕悷褰掓煃瑜滈崜娆擃敋閿濆鏁冮柨鏇楀亾闂佸崬娲︾换婵嬫濞戞艾顣洪梺璇叉禋閸ｏ絽顫忓ú顏勫窛濠电姴瀚崳顔界節濞堝灝鏋ら柡浣割煼閵嗕礁鈻庤箛鏇熸畷闂佸憡娲﹂崑鍛存倵椤掍胶绠鹃柡澶嬪灥閹垶绻涢崗鑲╂噧闁宠绉规俊鑸靛緞鐎ｎ剙甯?
     */
    private int countVisibleSegments(int categoryIndex, boolean positive) {
        int count = 0;
        for (BarChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (positive && value > 0) {
                count++;
            } else if (!positive && value < 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洦顐介柕鍫濇处椤洟鏌￠崶銉ョ仾闁稿鏅涢埞鎴︽偐鐎圭姴顥濈紓浣哄С閸楁娊寮诲☉銏犖ㄩ柟瀛樼箖閸ｇ儤鎱ㄩ敐鍛崳缂佽鲸鎸荤粭鐔煎炊瑜岄崙浠嬫倵鐟欏嫭绀嬫繛浣冲懎寮查梻浣虹帛椤ㄥ懘鎮ц箛娑樼厺闁哄啫鐗婇崐鐢告煥濠靛棝顎楀ù婊勭矋閵囧嫰鏁愰崱娆忓绩闂佸搫鐭夌换婵嗙暦閻撳簶鏀介柟閭﹀帨瑜嶉—鍐Χ閸℃鐟茬紓渚囧枟閹告悂鎮鹃悜钘夌疀闁绘鐗冮幏濠氭煟鎼淬劍娑у鐟帮躬瀹曘儵宕ㄧ€涙ǚ鎷洪梺鍛婄☉閿曪箓鎯屾繝鍥ㄥ仩婵鍘ф禍浼存煕閳规儳浜?
     */
    private double getCategoryPositiveTotal(int categoryIndex) {
        double total = 0D;
        for (BarChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value > 0) {
                total += value;
            }
        }
        return total;
    }

    /**
     * 闂傚倷娴囧畷鍨叏瀹曞洦顐介柕鍫濇处椤洟鏌￠崶銉ョ仾闁稿鏅涢埞鎴︽偐鐎圭姴顥濈紓浣哄С閸楁娊寮诲☉銏犖ㄩ柟瀛樼箖閸ｇ儤鎱ㄩ敐鍛崳缂佽鲸鎸荤粭鐔煎炊瑜岄崙浠嬫倵鐟欏嫭绀嬫繛浣冲懎寮查梻浣虹帛椤ㄥ懘鎮ц箛娑樼厺闁哄啫鐗婇崐鐢告煥濠靛棝顎楀ù婊勭矋閵囧嫰鏁愰崱娆忓绩闂佸搫鐭夌换婵嗙暦閻撳簶鏀介柟閭﹀帨椤忓棛纾藉ù锝嗗絻娴滈箖鏌ｆ惔顖滅У闁哥姵鐗滄竟鏇°亹閹烘挴鎷婚梺绋挎湰閸戝綊宕甸悢鎼炰簻闁规儳鐡ㄩ妵婵嬫煛鐏炲墽鈽夋い顓滃姂瀹曟﹢顢樿閽戝姊?
     */
    private double getCategoryNegativeTotal(int categoryIndex) {
        double total = 0D;
        for (BarChartSeries series : seriesList) {
            double value = series.getValues().get(categoryIndex);
            if (value < 0) {
                total += value;
            }
        }
        return total;
    }

    /**
     * 闂傚倷娴囬褏鎹㈤幇顔藉床闁归偊鍎靛☉妯锋瀻闁规崘娅曟潏鍫ユ⒑缂佹ɑ鈷掗柛妯犲懎顥氱憸鐗堝笚閻撴洟鐓崶銊﹀碍闁诡喗鍨归幃鐗堢附閸涘﹦鍘介棅顐㈡处閹稿墽绮绘导瀛樺€垫慨姗€妫跨花浠嬫煙瀹勬壆绉洪柟顔哄灲閹煎綊宕烽銊у簥濠电姷顣藉Σ鍛村垂閹惰棄纾块柕鍫濇川閻鏌涢鐘插姕闁绘挻娲熼弻鈥愁吋鎼粹€愁潽闂侀潧妫楅崯鎾蓟閵堝鍨傛い鎰╁灮娴煎矂姊?Y 闂傚倸鍊烽懗鍫曪綖鐎ｎ喖绀嬫い鎰╁焺濞兼棃姊绘担铏瑰笡闁绘鍘剧槐鐐存媴闁稓绠?
     */
    private int valueToY(double value, int plotTop, int plotBottom, ChartValueRange valueRange) {
        double ratio = (value - valueRange.getMin()) / (valueRange.getMax() - valueRange.getMin());
        return plotBottom - (int) Math.round(ratio * (plotBottom - plotTop));
    }

    private double resolveStackSegmentPercent(double value, double positiveTotal, double negativeTotal) {
        if (Double.compare(value, 0D) > 0) {
            if (Double.compare(positiveTotal, 0D) == 0) {
                return 0D;
            }
            return value / positiveTotal * 100D;
        }
        if (Double.compare(value, 0D) < 0) {
            if (Double.compare(negativeTotal, 0D) == 0) {
                return 0D;
            }
            return value / Math.abs(negativeTotal) * 100D;
        }
        return 0D;
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒娴ｈ櫣銆婇柡鍛矒閵嗗啯绻濋崒婊勬婵犻潧鍊婚…鍫ョ嵁閵忊€茬箚闁靛牆鍊告禍鍓х磽閸屾氨孝闁哄牜鍓涢幑銏犫槈閵忕娀鍞跺┑鐘绘涧閻楀懐鍒掗悽鍛娾拺缂備焦顭囨竟鍕磼閼艰埖纭堕柛鎺撳浮瀹曞綊顢曢妶鍌涙暤闂備胶鎳撻崥鈧紒鈧担鍦洸?
     * 濠电姷鏁搁崑鐐差焽濞嗘挸瑙﹂悗锝庡枟閺咁亪姊绘担鍛婂暈閽冭京绱掔€ｎ偅灏垫俊鍙夊姍楠炴帡骞婂畷鍥ф灈鐎规洦鍋婂畷鐔碱敇閻愯尙鐣遍梻鍌氬€风欢姘焽閼姐倖瀚婚柣鏃傚帶缁€澶屸偓骞垮劚椤︿即宕戦崒鐐村€垫繛鎴烆伆閹寸偛鍨旈柟缁㈠枟閻撴瑩鏌熼鍡楀暞濮ｆ劙姊烘潪鎵槮闁绘牜鍘ч～蹇撁洪宥嗘櫔闂佸憡渚楅崹鎵暜閵娧呯＝濞达絼绮欓崫铏圭磼鐠囨彃顏柛鎺撳笒椤繈顢栭悙瀛樸仢妞ゃ垺妫冨畷顏堝礃椤忓嫭鍎┑鐘垫暩閸嬬偤宕归崼鏇炵闁告縿鍎抽惌鎾绘煟閹达絾顥夌紒鈧崘銊㈡斀闁绘ê鐤囨竟妯肩磼椤愩垻效闁诡喖缍婂畷顐﹀礋椤掍礁鍓甸梺鑽ゅС閻掞箓鎮￠垾鎰佹綎婵炲樊浜堕弫鍐煟閺冨洦鑵瑰瑙勬礋濮婄粯鎷呴崨濠傛殘闂佺粯顨嗛〃濠囧箖瑜斿顕€宕掑☉姗嗕紩闂備胶绮濠氣€﹂崼銏☆偨闁绘劗鍎ら悡娑氣偓骞垮劚妤犳悂鐛弽顓熺厱闁哄倽顕ч崝锕傛煛鐏炵硶鍋撻幇浣告倯闂佸憡渚楅崰鏍玻濞戞﹩娓婚柕鍫濇缁椦囨煕韫囨枏鎴炵┍?
     */
    private void drawStackedValueLabel(Graphics2D g, Font valueFont, double rawValue, double percent,
                                       int x, int y, int barWidth, int barHeight, boolean positive, int zeroY) {
        LABEL_RENDERER.drawStackedValueLabel(
                g, valueFont, valueLabelColor, formatStackedValueLabel(rawValue, percent),
                x, y, barWidth, barHeight, positive, zeroY,
                minInsideLabelHeight, showSmallStackLabelOutside, externalLabelGap
        );
    }

    private void drawValueLabel(Graphics2D g, Font valueFont, double value, int x, int y,
                                int barWidth, int barHeight, int zeroY) {
        LABEL_RENDERER.drawValueLabel(
                g, valueFont, valueLabelColor, formatValue(value),
                value, x, y, barWidth, barHeight, zeroY
        );
    }

    /**
     * 缂傚倸鍊搁崐鎼佸磹閻戣姤鍊块柨鏇炲€归弲顏堟⒒?X 闂傚倷绀侀幖顐λ囬柆宥呯；婵炴垯鍩勯弫瀣亜閹捐泛校闁稿鐗楅妵鍕箛閸洘顎嶉梺绋块鐎涒晠濡甸崟顖氱睄闁稿本绋掗悵顏堟⒑閹肩偛濮€闁稿鍔楀Σ鎰板箳閹存梹顫嶅┑掳鍊愰崑鎾绘倵濮橆剟鍙勯柡?
     */
    private void drawXAxisLabels(Graphics2D g, int plotLeft, int plotBottom, int plotWidth, Font labelFont) {
        g.setFont(labelFont);
        g.setColor(labelColor);
        FontMetrics metrics = g.getFontMetrics();
        double categoryWidth = (double) plotWidth / categories.size();
        for (int i = 0; i < categories.size(); i++) {
            String category = Optional.ofNullable(categories.get(i)).orElse("");
            int textWidth = metrics.stringWidth(category);
            int x = (int) Math.round(plotLeft + i * categoryWidth + (categoryWidth - textWidth) / 2D);
            int y = plotBottom + metrics.getAscent() + 6;
            g.drawString(category, x, y);
        }
    }

    /**
     * 闂傚倷娴囧畷鐢稿窗閹扮増鍋￠弶鍫氭櫅缁躲倕螖閿濆懎鏆為柛濠勬暬閺岋綁鏁愰崨鏉款伃闁诲骸鐏氶悡锟犲箖瑜版帒鐐婇柕濞垮劤缁佺兘姊虹拠鑼闁荤喆鍎撮悘瀣⒑缂佹﹩娈旈柣妤€绻橀幆灞炬償閳藉棛鍞甸梺鑽ゅ枎鎼存粓鐓鍕厸濞达絽鎽滆倴婵炲濯寸粻鎾诲箠閺嶎厼鐓涘ù锝呮贡娴滅兘姊婚崒娆掑厡妞ゎ厼鐗忛幑銏ゅ箛閻楀牆浜遍梺鍛婁緱閸犳氨绮绘ィ鍐╃厱闁靛鍠栨晶顖炴煕鐎ｃ劌濮傞柡灞炬礃缁绘稖顦叉繛灞傚€濆畷銏犫枎閹剧补鎷洪梺鍛婄☉閿曘倗绮顓滀簻闁靛鍎哄Ο鈧悗瑙勬礋濞佳団€﹂妸鈺佸窛妞ゅ繐瀚竟鏇㈡煟閻斿摜鎳冮悗姘煎櫍閹繝顢涘鍛紲闂佺粯锕㈠褔寮搁幋锔界叆婵炴垶鐟ユ慨鍥ㄣ亜椤撴粌濮傜€规洖宕灃濠电姴鍠氬Λ蹇涙⒒閸屾瑧鍔嶉悗绗涘懐鐭欓柟杈鹃檮閸ゆ劖銇勯弽顐粶缂佺姵鑹鹃妴鎺戭潩閿濆懍澹曢柣搴ゎ潐濞叉﹢鎳濇ィ鍐ㄧ厺閹兼番鍊楅悿鈧梺鍝勫暙閸嬪棗危椤掑嫭鈷掗柛灞剧懅鐠愪即鏌涚€Ｑ冧壕闂備胶顭堥鍡涘箲閸ヮ剙钃熸繛鎴欏灩缁犲磭鈧箍鍎遍幊搴ｇ矈椤斿墽纾藉〒姘搐濞呮﹢鏌涢妸锕€鈻曟鐐茬箳閳ь剨缍嗛崰鏍ф纯闂備礁澹婇崑渚€宕瑰ú顏嶆晩鐎广儱顦伴埛鎴︽煠濞村娅嗛柛銈傚亾闁诲孩顔栭崰娑樷枖濞戔懇鈧箓宕归鍛缓闂侀€炲苯澧ǎ?
     */
    private Color resolveSeriesColor(BarChartSeries series, int index) {
        return Optional.ofNullable(series.getColor()).orElse(DEFAULT_PALETTE.get(index % DEFAULT_PALETTE.size()));
    }

    /**
     * 闂傚倷娴囧畷鐢稿窗閹扮増鍋￠弶鍫氭櫅缁躲倕螖閿濆懎鏆為柛濠囨涧闇夐柣妯烘▕閸庡繘鏌ｉ幇顒婅含闁哄瞼鍠撻幉鎾礋椤愩埄娼庨梻渚€鈧偛鑻晶顕€鏌涢悢鍛婂唉妤犵偛妫濆畷姗€顢欓懖鈺婃Ч婵＄偑鍊栫敮濠囨倿閿曞偆鏁婄€广儱顦伴埛鎴犵磼鐎ｎ亜鐨″顐ｈ壘椤潡鎮烽幍顔尖叡闂侀€炲苯澧柣蹇斿哺閹囨偐閼碱剚娈鹃柣鐘叉惈閹虫瑦鎯旈…鎴炴櫈闂佽姤锚椤︻喗绔?
     * 闂傚倸鍊风粈渚€骞栭锕€绠悗锝庡枛闂傤垶鏌ㄥ┑鍡╂Ц闁绘帒鐏氶妵鍕箳瀹ュ牆鍘″銈忕稻閻擄繝骞婂璺虹厸闁告劧绲芥禍鐐箾閸繄浠㈤柡瀣〒缁辨帞绱掑Ο铏逛患闂侀€炲苯澧柣蹇旀皑閸掓帡骞樺畷鍥ㄦ闂佽法鍠撴慨鎾础閹惰姤鐓熼柟鑸妽濞呭懎霉濠婂牏鐣洪柡灞诲€楅幉鎾礋椤掑倸鍤掗梻浣风串缁犳挻绂嶉崼鏇炶摕婵炴垯鍨瑰敮閻熸粍绮岃灋閹兼惌娼挎禍婊堟煙閹规劖鐝柟鎻掓憸缁辨帡顢欏▎鎯ф闁剧粯鐗犻幃妤呮晲鎼粹€愁潾濡炪倧瀵岄崳锝咁潖濞差亜绀堥柟缁樺笂缁ㄨ鈹戦埥鍡椾簼妞ゃ劌顦垫俊鐢稿箛閺夋寧顥濋柣鐘叉搐婢т粙宕伴弽顓炲瀭闁诡垎鍛闂佹悶鍎弲鈺呭绩閵娾晜鈷掑ù锝呮啞鐠愨剝銇勯鐐靛ⅵ閽樻繈寮堕崼姘珕闁稿鐗楅妵鍕箛閸洘顎嶉梺绋块鐎涒晠濡甸崟顖氭闁割煈鍠掗幐鍐⒑閸濆嫯瀚伴柣妤佹崌楠炲啰鎲撮崟顒€顎撳┑鐐存綑椤戝懏绂嶆ィ鍐╃厸闁稿本锚閸旀艾霉濠婂牏鐣洪柡灞诲€楅幉鎾礋椤掑倸鍤掗梻浣风串缁犳挻绂嶉崼鏇炶摕婵炴垯鍨瑰敮閻熸粍绮岃灋閹兼惌娼挎禍婊堟煙閹规劖鐝柟鎻掓憸缁辨帡顢欏▎鎯ф濡炪値浜滈崯鐗堢閹间礁鍐€鐟滃本绔?
     */
    private ChartValueRange resolveValueRange() {
        return RANGE_RESOLVER.resolve(categories, seriesList, stacked, percentStacked, minValue, maxValue);
    }


    /**
     * 闂傚倸鍊风粈浣革耿闁秮鈧箓宕奸妷瀣喘椤㈡稑顭ㄩ崨顖ｆЦ闂佽鍑界紞鍡涘窗閺嶎偆鐭嗛柛顐熸噰閸嬫捇鐛崹顔煎濠碘槅鍋呴悷銉╁煝閹捐閱囬柡鍥╁枔閸樿鲸绻濋悽闈浶㈤柟鍐查叄钘熺€广儱顦伴崐鐢告煕椤垵浜炲褏鏁搁埀顒冾潐濞叉牗鏅舵惔銊ョ劦妞ゆ帒瀚☉褔鏌熺拠褏绡€鐎殿喗鐓￠獮鏍ㄦ媴閸︻厼骞嶉梺璇插缁嬫帡鏁嬫繝娈垮枛椤︾敻寮婚敐澶婄労闁告劑鍔庢禒濂告⒑?Y 闂傚倷绀侀幖顐λ囬柆宥呯；闁绘劕妯婇悞鑺ョ箾閸℃ɑ灏柡鍕╁劦閺屾洝绠涙繝鍌氣拤闂佺娴烽崰鏍蓟閵娾晛绫嶉柛銉ｅ劗閸嬫捇鍩€椤掑嫭鐓?
     */
    private List<Double> createTicks(ChartValueRange valueRange) {
        List<Double> ticks = new ArrayList<Double>();
        double step = (valueRange.getMax() - valueRange.getMin()) / (yAxisTickCount - 1);
        for (int i = 0; i < yAxisTickCount; i++) {
            ticks.add(valueRange.getMin() + step * i);
        }
        return ticks;
    }

    /**
     * 闂傚倸鍊风粈渚€骞栭锕€绠悗锝庡枛闂傤垶鏌ㄥ┑鍡╂Ц闁绘帒鐏氶妵鍕箳瀹ュ牆鍘″銈忕稻閻擄繝寮婚敓鐘插耿婵炲棗绻戦悘鍫ユ倵鐟欏嫭绀冮柛鏃€鐟ラ悾宄拔旈崨顔兼異闂佸疇妗ㄧ欢鈥澄涢弮鍫熺厽閹兼番鍩勯崯蹇涙煕閻樺磭澧遍柟骞垮灲楠炲洭顢欓悷棰佸闂佸壊鐓堥崑鍡涘矗閳ь剚绻?
     */
    private String formatValue(double value) {
        return decimalFormat.format(value);
    }

    /**
     * 闂傚倸鍊烽懗鍫曪綖鐎ｎ喖绀嬫い鎰╁焺濞兼棃姊绘担铏瑰笡闁绘鍘惧濠囧箚瑜滈悞鑺ャ亜韫囨挻顥戦柛瀣尭閳藉鈻嶉搹顐㈢仴闁崇粯鎹囧畷濂稿即閻斿搫骞堝┑鐘垫暩婵挳宕愰幖浣哥畺闁硅揪闄勯悡鐔镐繆閵堝懎鏋ら柣鎺斿亾閵囧嫰寮撮鍡櫺氱紓浣哄У閻╊垶鐛鈧獮鍥ㄦ媴閸涘鍞查梻?
     * 闂傚倸鍊峰ù鍥儍椤愶箑绀嬫い鎰╁灩琚橀梻鍌欑劍閹爼宕濈仦缁㈡婵繂鐭堥弳浼存⒒娴ｅ憡鎯堥柛濠呮閳绘棃寮撮姀鐘茬€繝鐢靛У绾板秹鎮￠弴鐔虹闁糕剝锚閻忋儵寮介敓鐘崇厾闁搞劍绋掑▍鍥煃瑜滈崜姘额敊閺嶎厼绐楅柡宥庡亝瀹曟煡鎮楅敐搴″缂佺姵妫冮弻娑樷槈閸楃偞鐏堝銈嗘礉妞存悂骞堥妸銉庣喖宕崟顒佺槪婵犵數鍋涢悧鍛村础閹惰棄钃熸繛鎴欏灩濡﹢鎮归崶銊ョ祷妞ゎ偄鎳樺楦裤亹閹烘繃顥栭梺绋跨箲閿氶柣锝呭槻閳规垹鈧綆鍋勬禍褰掓⒑閸撹尙鍘涢柛瀣笚缁傛帡骞橀瑙ｆ嫼闂佸憡绋戦敃銈囩箔閹烘挷绻嗘い鎰剁秵濞堟瑩鏌曢崶銊ュ鐎规洜顭堣灃闁逞屽墲缁?
     */
    private String formatAxisValue(double value) {
        return percentStacked ? formatValue(value) + "%" : formatValue(value);
    }

    /**
     * 闂傚倸鍊烽悞锕傛儑瑜版帒鍨傚┑鐘宠壘缁愭鏌熼悧鍫熺凡闁搞劌鍊归幈銊ノ熼幐搴ｃ€愰梻浣稿船濞差參寮诲☉銏犵労闁告劗鍋撻悾鍓佺磽閸屾氨孝闁哄牜鍓涢幑銏犫槈閵忕娀鍞跺┑鐘绘涧閻楀懐鍒掗悽鍛娾拺缂備焦顭囨竟鍕磼閼艰埖纭堕柛鎺撳浮閹粙宕ㄦ繝鍌楀亾闁垮浜滈柟鍝勭Х閸忓矂鏌嶈閸擄箓宕抽敐澶婅摕闁跨喓濮撮柋鍥ㄣ亜閹扳晛鐏╂鐐茬Ч濮?
     */
    private String formatStackedValueLabel(double rawValue, double percent) {
        if (stackLabelMode == StackLabelMode.VALUE) {
            return formatValue(rawValue);
        }
        if (stackLabelMode == StackLabelMode.PERCENT) {
            return formatValue(percent) + "%";
        }
        return formatValue(rawValue) + "(" + formatValue(percent) + "%)";
    }

    /**
     * 闂傚倸鍊风粈浣圭珶婵犲洤纾婚柛鏇ㄥ€犲☉妯滄棃宕熼埡鈧Ч妤呮⒑閸濆嫬鏆欓柣妤€妫濆畷姗€鍩€椤掑嫭鈷戦柟鑲╁仜閸旀鏌￠崨顏呮珚鐎规洘绻堥、姘跺焵椤掑嫬绠栨慨妞诲亾闁诡喗鐟ч埀顒佺⊕椤洭藝椤撱垺鈷戦梻鍫熺⊕婢跺嫰鏌ょ憴鍕闁诲繑甯″娲倷閽樺濮庣紒鍓ц檸閸樻儳鈽夐悽绋跨劦妞ゆ帒瀚埛?
     */
    @Getter
    @AllArgsConstructor
    public enum StackLabelMode {
        VALUE("value"),
        PERCENT("percent"),
        VALUE_PERCENT("value_percent");
        private String desc;
    }
}
