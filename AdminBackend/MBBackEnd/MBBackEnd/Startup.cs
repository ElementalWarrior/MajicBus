using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(MBBackEnd.Startup))]
namespace MBBackEnd
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
