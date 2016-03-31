using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.Classes
{
    public class Utility
    {
        public static string SanitizePhoneNumber(string unsanitized)
        {
            string retStr = "";
            for(int i = 0; i < unsanitized.Length; i++)
            {
                if((int)unsanitized[i] >= (int)'0' && (int)unsanitized[i] <= (int)'9')
                {
                    retStr += unsanitized[i];
                }
            }
            return retStr;
        }
    }
}