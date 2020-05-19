var domains = {
    "googleapis.com": 1,
    "googlecode.com": 1,
    "googleusercontent.com": 1,
    "ggpht.com": 1,
    "gvt1.com": 1,
    "gstatic.com": 1,
    "gmail.com": 1,
    "googlegroups.com": 1,
    "googleratings.com": 1,
    "google.com.hk": 1,
    "google.com.tw": 1,
    "google.co.jp": 1,
    "google.co.kr": 1,
    "google.co.th": 1,
    "google.com.vn": 1,
    "google.com.sg": 1,
    "google.com.my": 1,
    "google.com.ru": 1,
    "google.ae": 1,
    "google.com.sa": 1,
    "google.co.in": 1,
    "google.com.np": 1,
    "google.de": 1,
    "google.com.kw": 1,
    "google.com.co": 1,
    "google.fr": 1,
    "google.co.uk": 1,
    "google.it": 1,
    "google.gr": 1,
    "google.pt": 1,
    "google.es": 1,
    "google.co.il": 1,
    "google.ch": 1,
    "google.se": 1,
    "google.nl": 1,
    "google.be": 1,
    "google.at": 1,
    "google.pl": 1,
    "google.pt": 1,
    "google.es": 1,
    "google.fi": 1,
    "google.nl": 1,
    "google.co.hu": 1,
    "google.com.tr": 1,
    "google.ro": 1,
    "google.dk": 1,
    "google.no": 1,
    "google.com.au": 1,
    "google.co.nz": 1,
    "google.ca": 1,
    "google.com": 1,
    "google.com.mx": 1,
    "google.com.br": 1,
    "google.com.ar": 1,
    "google.cl": 1,
    "google.com.pe": 1,
    "google.com.eg": 1,
    "google.com.pa": 1,
    "google.lt": 1,
    "google.bi": 1,
    "google.pn": 1,
    "google.li": 1,
    "google.com.nf": 1,
    "google.vg": 1,
    "google.mw": 1,
    "google.fm": 1,
    "google.sh": 1,
    "google.cd": 1,
    "google.ms": 1,
    "google.co.cr": 1,
    "google.lv": 1,
    "google.ie": 1,
    "google.co.gg": 1,
    "google.co.je": 1,
    "google.pr": 1,
    "google.com.py": 1,
    "google.gm": 1,
    "google.td": 1,
    "google.com.ua": 1,
    "google.co.ve": 1,
    "google.com.tr": 1,
    "google.com.mt": 1,
    "google.com.uy": 1,
    "google.hn": 1,
    "google.com.ni": 1,
    "google.gl": 1,
    "google.kz": 1,
    "google.sm": 1,
    "google.co.mu": 1,
    "google.as": 1,
    "google.uz": 1,
    "google.rw": 1,
    "google.cz": 1,
    "google.ru": 1,
    "google.rs": 1,
    "google.md": 1,
    "google.co.id": 1,
    "google.com.tj": 1,
    "thinkwithgoogle.com": 1,
    "googletagmanager.com": 1,
    "golang.org": 1,
    "tensorflow.org": 1,
    "wikimedia.org": 1,
    "wikipedia.org": 1,
    "epochtimes.com": 1
};
var hosts = [
    "scholar.google.com",
    "scholar.google.com.hk",
    "scholar.googleusercontent.com"
];
function FindProxyForURL(url, host) {
    if (hosts.indexOf(host) >= 0) {
        return "HTTPS us.fv.qqwx8.xyz:13389;HTTPS jp.free.iggimgcdn.com:443;HTTPS us.mzke5.buzz:443;";
    }
    else {
        var match = host.match(/([^.]*\.([a-z,A-Z]*|com\.[a-z]*|co\.[a-z]*))$/);
        if (match && match[1] && domains.hasOwnProperty(match[1])) {
            return "HTTPS cdn.poweris.xyz:7766;HTTPS www.shawns.xyz:4040;HTTPS www.withstranger.xyz:5555;";
        }
    }
    return "DIRECT;";
}
